package net.superkassa.models

import net.superkassa.annotations.Synonym
import org.json4s.CustomSerializer
import org.json4s.JsonAST.{JNull, JObject}
import scala.collection.breakOut

@Synonym("intermediate")
case class SearchResult(data: Map[Recommendation, Itinerary], isDealer: Boolean, isPossum: Boolean)

object SearchResult {

  object Serializer extends CustomSerializer[SearchResult](implicit formats => ({
    case JObject(jFields) =>
      val fields = jFields.toMap
      require(fields.contains("segments") && fields.contains("recommendations"), "segments and recommendations should be defined is search result!")
      val segments = fields("segments").extract[Map[String, Segment]]
      val recommendations = fields("recommendations").extract[List[Recommendation]]
      val isDealer = fields.get("__is_dealer").exists(_.extract[Boolean])
      val isPossum = fields.get("__play_possum").exists(_.extract[Boolean])
      val data = {
        recommendations.map { recommendation =>
          val recommendationSegments = segments.filterKeys(recommendation.segmentRecommendations.keySet.contains)
          recommendation -> Itinerary(recommendationSegments.values)
        }(breakOut[List[Recommendation], (Recommendation, Itinerary), Map[Recommendation, Itinerary]])
      }
      SearchResult(data, isDealer, isPossum)
  }, {
    case _ => JNull
  }))

}