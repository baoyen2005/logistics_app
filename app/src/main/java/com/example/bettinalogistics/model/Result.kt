package com.example.bettinalogistics.model

class Result {
    var destination_address: List<String>? = null
    var origin_address: List<String>? = null
    var rows: List<Rows> ?=  null
    var status: String? = null
}

class Distance{
    var text : String? = null
    var value: Int? = null
}

class  Duration{
    var text : String? = null
    var value: Int? = null
}

class Elements{
    var distance: Distance?= null
    var duration:Duration? = null
    var status: String? = null
}

class Rows{
    var elements: List<Elements>? = null
}