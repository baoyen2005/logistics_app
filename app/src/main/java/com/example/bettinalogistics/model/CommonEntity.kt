package com.example.bettinalogistics.model

class CommonEntity {
    var icon : Int? = null
    var title: String? = null

    constructor()

    constructor(icon : Int, title: String){
        this.icon = icon
        this.title = title
    }
}