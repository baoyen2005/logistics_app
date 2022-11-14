package com.example.bettinalogistics.model

class CommonEntity {
    var icon : Int? = null
    var title: String? = null
    var description: String? = null
    var isEdit: Boolean? = false

    constructor()

    constructor(icon : Int, title: String){
        this.icon = icon
        this.title = title
    }
    constructor( title: String, description:String, isEdit: Boolean){
        this.description = description
        this.isEdit = isEdit
        this.title = title
    }
}