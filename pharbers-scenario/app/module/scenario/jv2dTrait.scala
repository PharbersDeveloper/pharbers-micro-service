package module.scenario

import com.mongodb.casbah.Imports.{DBObject, MongoDBList, MongoDBObject}
import play.api.libs.json.{JsObject, JsValue}

trait jv2dTrait {

    def jv2d(js: JsValue): DBObject = {
        def connectTransform(key: String, js: JsValue): DBObject = {
            val tmp = (js \ key).as[List[Map[String, JsValue]]].map { m =>
                val builder = MongoDBObject.newBuilder
                builder += "type" -> m("type").as[String]
                builder += "id" -> m("id").as[String]
                val value = m("relationship").as[JsObject]
                        .value("value").as[List[Map[String, JsValue]]]
                        .map(x =>
                            DBObject(
                                "index" -> int2Integer(x("index").as[Int]),
                                "news" -> x("news").as[String]
                            )
                        )
                builder += "relationship" -> DBObject("value" -> MongoDBList(value: _*).underlying)
                builder.result
            }
            DBObject(key -> MongoDBList(tmp: _*).underlying)
        }

        def destTransform(js: JsValue): DBObject = connectTransform("connect_dest", js)

        def repTransform(js: JsValue): DBObject = connectTransform("connect_rep", js)

        def goodsTransform(js: JsValue): DBObject = connectTransform("connect_goods", js)

        def resourceTransform(js: JsValue): DBObject = {
            val connect_reso = (js \ "connect_reso").as[List[Map[String, JsValue]]]
            val day = connect_reso.find(_ ("type").as[String] == "day").map { x =>
                DBObject(
                    "type" -> x("type").as[String],
                    "id" -> x("id").as[String],
                    "relationship" -> DBObject(
                        "value" -> int2Integer(
                            x("relationship").asOpt[Map[String, JsValue]]
                                    .get("value").as[Int]
                        )
                    )
                )
            }.get

            val money = connect_reso.find(_ ("type").as[String] == "money").map { x =>
                DBObject(
                    "type" -> x("type").as[String],
                    "id" -> x("id").as[String],
                    "relationship" -> DBObject(
                        "value" -> long2Long(
                            x("relationship").asOpt[Map[String, JsValue]]
                                    .get("value").as[Long]
                        )
                    )
                )
            }.get

            DBObject("connect_reso" -> MongoDBList(day, money).underlying)
        }

        def destRepTransform(js: JsValue): DBObject = {
            (js \ "dest_rep").asOpt[List[Map[String, JsValue]]] match {
                case Some(lst) =>
                    val tmp = lst.map { m =>
                        val builder = MongoDBObject.newBuilder
                        builder += "dest_id" -> m("dest_id").as[String]
                        builder += "rep_id" -> m("rep_id").as[String]
                        builder += "relationship" -> DBObject()
                        builder.result
                    }
                    DBObject("dest_rep" -> MongoDBList(tmp: _*).underlying)
                case None => DBObject()
            }
        }

        def destGoodsTransform(js: JsValue): DBObject = {
            val tmp = (js \ "dest_goods").as[List[Map[String, JsValue]]].map { m =>
                val relationship = m("relationship").asOpt[Map[String, JsValue]].map { r =>
                    val compete_goods = r("compete_goods").as[List[Map[String, JsValue]]].map { cm =>
                        DBObject(
                            "goods_id" -> cm("goods_id").as[String],
                            "sales" -> long2Long(cm("sales").as[Long]),
                            "sales_growth" -> double2Double(cm("sales_growth").as[Double]),
                            "share" -> double2Double(cm("share").as[Double]),
                            "share_change" -> double2Double(cm("share_change").as[Double]),
                            "life_cycle" -> cm("life_cycle").as[String]
                        )
                    }
                    DBObject(
                        "potential" -> long2Long(r("potential").as[Long]),
                        "potential_growth" -> double2Double(r("potential_growth").as[Double]),
                        "contri_rate" -> double2Double(r("contri_rate").as[Double]),
                        "share" -> double2Double(r("share").as[Double]),
                        "share_change" -> double2Double(r("share_change").as[Double]),
                        "sales" -> long2Long(r("sales").as[Long]),
                        "sales_growth" -> double2Double(r("sales_growth").as[Double]),
                        "target_level" -> int2Integer(r("target_level").as[Int]),
                        "life_cycle" -> r("life_cycle").as[String],
                        "compete_goods" -> MongoDBList(compete_goods: _*).underlying
                    )
                }.get
                DBObject(
                    "dest_id" -> m("dest_id").as[String],
                    "goods_id" -> m("goods_id").as[String],
                    "relationship" -> relationship
                )
            }
            DBObject("dest_goods" -> MongoDBList(tmp: _*).underlying)
        }

        def destGoodsRepTransform(js: JsValue): DBObject = {
            (js \ "dest_goods_rep").asOpt[List[Map[String, JsValue]]] match {
                case Some(lst) =>
                    val tmp = lst.map { m =>
                        val builder = MongoDBObject.newBuilder
                        builder += "dest_id" -> m("dest_id").as[String]
                        builder += "goods_id" -> m("goods_id").as[String]
                        builder += "rep_id" -> m("rep_id").as[String]
                        val relationship = m("relationship").as[Map[String, JsValue]]
                        builder += "relationship" -> DBObject(
                            "user_input_money" -> long2Long(relationship("user_input_money").as[Long]),
                            "budget_proportion" -> double2Double(relationship("budget_proportion").as[Double]),
                            "user_input_day" -> int2Integer(relationship("user_input_day").as[Int]),
                            "user_input_target" -> long2Long(relationship("user_input_target").as[Long]),
                            "target_growth" -> double2Double(relationship("target_growth").as[Double]),
                            "achieve_rate" -> double2Double(relationship("achieve_rate").as[Double])
                        )
                        builder.result
                    }
                    DBObject("dest_goods_rep" -> MongoDBList(tmp: _*).underlying)
                case None => DBObject()
            }
        }

        val builder = MongoDBObject.newBuilder

        builder += "phase" -> int2Integer((js \ "phase").as[Int])
        builder += "name" -> (js \ "name").as[String]
        builder += "report_id" -> (js \ "report_id").as[String]

        builder ++= destTransform(js)
        builder ++= repTransform(js)
        builder ++= goodsTransform(js)
        builder ++= resourceTransform(js)

        builder ++= destRepTransform(js)
        builder ++= destGoodsTransform(js)
        builder ++= destGoodsRepTransform(js)

        builder.result
    }
}
