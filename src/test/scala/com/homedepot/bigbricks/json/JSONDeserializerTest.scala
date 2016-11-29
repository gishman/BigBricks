package com.homedepot.bigbricks.json

import com.recipegrace.biglibrary.core.BaseTest
import net.liftweb.json.Extraction._
import net.liftweb.json.{DefaultFormats, _}

/**
  * Created by fjacob on 8/16/15.
  */
class JSONDeserializerTest extends BaseTest {
  implicit val formats = DefaultFormats

  test(" json test") {

    val list = List("ferosh", "jacob")
    val json = compactRender(decompose(list))
    println(json)

    val parsedJSON=   parse(json)   .extract[List[String]]

    parsedJSON shouldBe list

  }

}
