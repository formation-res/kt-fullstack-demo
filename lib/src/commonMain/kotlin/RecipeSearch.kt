@file:Suppress("unused")

package recipesearch

import com.jillesvangurp.ktsearch.*
import com.jillesvangurp.ktsearch.repository.IndexRepository
import com.jillesvangurp.searchdsls.querydsl.*

class RecipeSearch(
    private val repository: IndexRepository<Recipe>,
    private val searchClient: SearchClient,
) {

    suspend fun healthStatus(): ClusterHealthResponse {
        return searchClient.clusterHealth()
    }

    suspend fun createNewIndex() {
        repository.createIndex(block = {
            mappings {
                text("allfields")
                text(Recipe::title) {
                    copyTo = listOf("allfields")
                    fields {
                        text("autocomplete") {
                            analyzer = "autocomplete"
                            searchAnalyzer = "autocomplete_search"
                        }
                    }
                }
                text(Recipe::description) {
                    copyTo = listOf("allfields")
                }
                text(Recipe::ingredients)
                text(Recipe::directions)
                number<Int>(Recipe::prepTimeMin)
                number<Int>(Recipe::cookTimeMin)
                number<Int>(Recipe::servings)
                keyword(Recipe::tags) {
                    copyTo = listOf("allfields")
                }
                objField(Recipe::author) {
                    text(Author::name) {
                        copyTo = listOf("allfields")
                    }
                    keyword(Author::url)
                }
            }
            settings {
                replicas = 0
                shards = 1
                analysis {
                    tokenizer("autocomplete") {
                        type = "edge_ngram"
                        put("min_gram", 2)
                        put("max_gram", 10)
                        put("token_chars", listOf("letter"))
                    }

                    analyzer("autocomplete") {
                        put("tokenizer", "autocomplete")
                        put("filter", listOf("lowercase"))
                    }
                    analyzer("autocomplete_search") {
                        put("tokenizer", "lowercase")
                    }
                }
            }
        })
    }

    suspend fun deleteIndex() {
        repository.deleteIndex()
    }

    suspend fun search(text: String, start: Int, hits: Int):
        SearchResults<Recipe> {
            val response = repository.search {
                    from = start
                    resultSize = hits
                    query = if(text.isBlank()) {
                        matchAll()
                    } else {
                        bool {
                            should(
                                matchPhrase(Recipe::title, text) {
                                    boost=2.0
                                },
                                match(Recipe::title, text) {
                                    boost=1.5
                                    fuzziness="auto"
                                },
                                match("allfields", text) {
                                    fuzziness="auto"
                                },
                                matchPhrasePrefix(Recipe::title, text) {
                                   boost=0.25
                                },
                                match(Recipe::directions, text) {
                                    boost=0.1
                                },
                                match(Recipe::ingredients, text) {
                                    boost=0.1
                                },
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

inline fun <reified T:Any> SearchResponse.toSearchResults(): SearchResults<T> {
    val response = this
    val collectedHits = mutableListOf<T>()
    response.parseHits<T>().forEach {
        collectedHits.add(it)
    }
    return SearchResults(response.total, collectedHits)
}

