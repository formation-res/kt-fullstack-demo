package recipesearch

import com.jillesvangurp.ktsearch.SearchResponse
import com.jillesvangurp.ktsearch.total
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.Serializable

@Serializable
data class RecipeSearchResponse<T : Any>(val totalHits: Long, val items: List<T>)

suspend fun <T:Any> Pair<SearchResponse, Flow<T?>>.toSearchResponse(): RecipeSearchResponse<T> {
    val (r,f) = this
    val collectedHits = mutableListOf<T>()
    f.collect {
        if(it != null)
        collectedHits.add(it)
    }
    return RecipeSearchResponse(r.total, collectedHits)
}


//suspend fun <T : Any> AsyncSearchResults<T>.toSearchResponse(): SearchResponse<T> {
//    val collectedHits = mutableListOf<T>()
//    this.mappedHits.collect {
//        collectedHits.add(it)
//    }
//    return SearchResponse(this.total, collectedHits)
//}
//// END search_response
