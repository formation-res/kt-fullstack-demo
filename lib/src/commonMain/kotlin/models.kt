package recipesearch

import com.jillesvangurp.ktsearch.SearchResponse
import com.jillesvangurp.ktsearch.total
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.Serializable


@Serializable
data class Recipe(
    val title: String,
    val description: String,
    val ingredients: List<String>,
    val directions: List<String>,
    val prepTimeMin: Int?,
    val cookTimeMin: Int?,
    val servings: Int?,
    val tags: List<String>?,
    val author: Author,
    val sourceUrl: String?
)

@Serializable
data class Author(val name: String, val url: String)

@Serializable
data class SearchResults<T : Any>(val totalHits: Long, val items: List<T>)
