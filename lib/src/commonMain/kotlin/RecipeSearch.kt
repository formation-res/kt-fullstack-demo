@file:Suppress("unused")

package recipesearch

import com.jillesvangurp.ktsearch.*
import com.jillesvangurp.ktsearch.repository.IndexRepository
import com.jillesvangurp.searchdsls.querydsl.bool
import com.jillesvangurp.searchdsls.querydsl.match
import com.jillesvangurp.searchdsls.querydsl.matchAll
import com.jillesvangurp.searchdsls.querydsl.matchPhrase
import kotlinx.coroutines.flow.Flow

class RecipeSearch(
    private val repository: IndexRepository<Recipe>,
    private val searchClient: SearchClient,
) {

    suspend fun healthStatus(): ClusterHealthResponse {
        return searchClient.clusterHealth()
    }

    suspend fun createNewIndex() {
        repository.createIndex(block = {
            settings {
                replicas = 0
                shards = 1
                // we have some syntactic sugar for adding custom analysis
                // however we don't hava a complete DSL for this
                // so we fall back to using put for things
                // not in the DSL
                addTokenizer("autocomplete") {
                    put("type", "edge_ngram")
                    put("min_gram", 2)
                    put("max_gram", 10)
                    put("token_chars", listOf("letter"))
                }
                addAnalyzer("autocomplete") {
                    put("tokenizer", "autocomplete")
                    put("filter", listOf("lowercase"))
                }
                addAnalyzer("autocomplete_search") {
                    put("tokenizer", "lowercase")
                }
            }
            mappings {
                text("allfields")
                text("title") {
                    copyTo = listOf("allfields")
                    fields {
                        text("autocomplete") {
                            analyzer = "autocomplete"
                            searchAnalyzer = "autocomplete_search"
                        }
                    }
                }
                text("description") {
                    copyTo = listOf("allfields")
                }
                number<Int>("prep_time_min")
                number<Int>("cook_time_min")
                number<Int>("servings")
                keyword("tags")
                objField("author") {
                    text("name")
                    keyword("url")
                }
            }
        })
    }

    suspend fun deleteIndex() {
        repository.deleteIndex()
    }

    suspend fun search(text: String, start: Int, hits: Int):
        SearchResults<Recipe> {
            val response: Pair<SearchResponse, Flow<Recipe?>> = repository.search {
                    from = start
                    resultSize = hits
                    query = if(text.isBlank()) {
                        matchAll()
                    } else {
                        bool {
                            should(
                                matchPhrase("title", text) {
                                    boost=2.0
                                },
                                match("title", text) {
                                    boost=1.5
                                    fuzziness="auto"
                                },
                                match("description", text)
                            )
                        }
                    }
            }
        return response.toSearchResults()
    }

    suspend fun autocomplete(text: String, start: Int, hits: Int):
        SearchResults<Recipe> {
            return repository.search {
                    from = start
                    resultSize = hits
                    query = if(text.isBlank()) {
                        matchAll()
                    } else {
                        match("title.autocomplete", text)
                    }

            }.toSearchResults()
        }
}

suspend fun <T:Any> Pair<SearchResponse, Flow<T?>>.toSearchResults(): SearchResults<T> {
    val (r,f) = this
    val collectedHits = mutableListOf<T>()
    f.collect {
        if(it != null)
            collectedHits.add(it)
    }
    return SearchResults(r.total, collectedHits)
}

