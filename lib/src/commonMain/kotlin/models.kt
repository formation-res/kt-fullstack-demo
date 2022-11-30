package recipesearch

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Recipe(
    val title: String,
    val description: String,
    val ingredients: List<String>,
    val directions: List<String>,
    @SerialName("prep_time_min")
    val prepTimeMin: Int?,
    @SerialName("cook_time_min")
    val cookTimeMin: Int?,
    val servings: Int?,
    val tags: List<String>?,
    val author: Author,
    @SerialName("source_url")
    val sourceUrl: String?
)

@Serializable
data class Author(val name: String, val url: String)

@Serializable
data class SearchResults<T : Any>(val totalHits: Long, val items: List<T>)
