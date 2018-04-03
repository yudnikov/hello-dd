package net.superkassa.datum

import net.superkassa.calculator.blacklist.BlacklistRule
import net.superkassa.calculator.rules.{BspRule, ClearingRule, SegmentBonusRule, TchRule}
import net.superkassa.calculator.thresholds.Threshold
import net.superkassa.models.Carrier
import net.superkassa.models.geography._
import org.json4s.Formats
import ru.yudnikov.database.postgres.PostgresSession
import ru.yudnikov.utils._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

trait DatabaseReader {

  val backendSession: PostgresSession
  val frontendSession: PostgresSession

  def portsToCitiesFuture: Future[Map[Port, City]] = {
    backendSession.executeQuery(s"SELECT * FROM ports") { rs =>
      Port(rs.getString("port_code")) -> City(rs.getString("city_code"))
    } map {
      _.toMap
    }
  }

  def thresholdsFuture(datumContext: DatumContext): Future[Set[Threshold]] = {
    backendSession.executeQuery(s"SELECT * FROM thresholds")(Threshold(_)(datumContext)).map(_.toSet)
  }

  def blacklistRulesFuture: Future[Set[BlacklistRule]] = {
    backendSession.executeQuery(s"SELECT * FROM gds_filters")(BlacklistRule(_)).map(_.toSet)
  }

  def segmentBonusRulesFuture(datumContext: DatumContext): Future[Set[SegmentBonusRule]] = {
    backendSession.executeQuery(s"SELECT * FROM segment_bonuses")(SegmentBonusRule(_, datumContext)).map(_.flatten.toSet)
  }

  def interlinesFuture: Future[Map[Carrier, Set[Carrier]]] = {
    val query = s"SELECT company_from, company_to FROM interlines WHERE approval IS NULL OR approval"
    backendSession.executeQuery(query) { rs =>
      Carrier(rs.getString("company_from")) -> Carrier(rs.getString("company_to"))
    } map { iterator =>
      iterator.toList.groupBy(_._1).mapValues(_.map(_._2).toSet)
    }
  }

  def clearingRulesFuture(datumContext: DatumContext): Future[Set[ClearingRule]] = {
    val fields = "id, validating_carrier, conditions, commission_type, commission_value, market, shop"
    val bspRulesFuture = backendSession.executeQuery(s"SELECT $fields FROM bsp_rules")(BspRule(_)(datumContext))
    val tchRulesFuture = backendSession.executeQuery(s"SELECT $fields FROM tch_rules")(TchRule(_)(datumContext))
    for {
      bspRules <- bspRulesFuture
      tchRules <- tchRulesFuture
    } yield {
      (bspRules.toList union tchRules.toList).toSet[ClearingRule]
    }
  }

  /*def portsToCitiesStringsFuture: Future[Map[String, String]] = {
    backendSession.executeQuery(s"SELECT * FROM ports") { rs =>
      rs.getString("port_code") -> rs.getString("city_code")
    } map {
      _.toMap
    }
  }*/

}
