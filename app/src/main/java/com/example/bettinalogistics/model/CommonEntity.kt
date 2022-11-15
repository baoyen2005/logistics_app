package com.example.bettinalogistics.model

class CommonEntity {
    var icon : Int? = null
    var title: String? = null
    var description: String? = null
    var isEdit: Boolean? = false
    constructor(icon : Int, title: String){
        this.icon = icon
        this.title = title
    }
    private var header: String? = null
    var destiny: String? = null
    var isHightLight: Boolean = false
    private var typeLayout: Int = 0
    private var counter: Int = 0
    private var dataToolTip: String? = null
    var isSpace: Boolean? = false
    var isHeader: Boolean? = false
    var isDashLine: Boolean? = false
    var descriptColor: Int? = null
    private var iconUrl: String? = null
    private var iconUrlVip: String? = null
    private var refundAmount: String? = null
    private var objData: Any? = null

    fun setIsSpace(isSpace: Boolean): CommonEntity {
        this.isSpace = isSpace
        return this
    }

    fun setIsHeader(isHeader: Boolean): CommonEntity {
        this.isHeader = isHeader
        return this
    }

    fun setIsDashLine(isDashLine: Boolean): CommonEntity {
        this.isDashLine = isDashLine
        return this
    }

    fun isHighLight(): Boolean {
        return isHightLight
    }

    fun setHightLight(hightLight: Boolean): CommonEntity {
        isHightLight = hightLight
        return this
    }

    fun setRefundAmountString(amount : String) : CommonEntity{
        refundAmount = amount
        return this
    }

    fun getRefundAmountString() : String?{
        return refundAmount
    }

    fun getTypeLayout(): Int {
        return typeLayout
    }

    fun setTypeLayout(typeLayout: Int): CommonEntity {
        this.typeLayout = typeLayout
        return this
    }

    fun getIconURl(): String? {
        return iconUrl
    }

    fun setIconUrl(url: String?): CommonEntity {
        this.iconUrl = url
        return this
    }

    fun getIconURlVip(): String? {
        return iconUrlVip
    }

    fun setIconUrlVip(url: String?): CommonEntity {
        this.iconUrlVip = url
        return this
    }

    fun getObjData(): Any? {
        return objData
    }

    fun setObjData(objData: Any?): CommonEntity {
        this.objData = objData
        return this
    }

    fun setDescriptColor(color: Int): CommonEntity {
        this.descriptColor = color
        return this
    }


    fun getCounter(): Int {
        return counter
    }


    fun setCounter(counter: Int): CommonEntity {
        this.counter = counter
        return this
    }

    fun setDestiny(destiny: String): CommonEntity {
        this.destiny = destiny
        return this
    }

    fun setHeader(header: String): CommonEntity {
        this.header = header
        return this
    }

    fun getHeader(): String {
        return header!!
    }

    constructor(title: String, descript: String?) {
        this.title = title
        this.description = descript
    }

    constructor(title: String, descript: String?, typeLayout: Int) {
        this.title = title
        this.description = descript
        this.typeLayout = typeLayout
    }

    constructor(header: String?, icon: Int, typeLayout: Int) {
        this.header = header
        this.icon = icon
        this.typeLayout = typeLayout
    }

    constructor(header: String?, typeLayout: Int) {
        this.header = header
        this.typeLayout = typeLayout
    }

    constructor() {}

    fun setDataToolTip(dataToolTip: String): CommonEntity {
        this.dataToolTip = dataToolTip
        return this
    }

    fun getDataToolTip(): String? {
        return dataToolTip
    }
}