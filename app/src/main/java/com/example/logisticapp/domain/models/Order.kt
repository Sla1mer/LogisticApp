package com.example.logisticapp.domain.models

class Order {
    var start: Point? = null
    var finish: Point? = null

    var nameStart: String? = null
    var nameFinish: String? = null
    var descProduct: String? = null
    var executor: String? = null
    var status: String? = null

    constructor() // Пустой конструктор для использования в Firebase

    constructor(
        start: Point?,
        finish: Point?,
        nameStart: String?,
        nameFinish: String?,
        descProduct: String?,
        executor: String?,
        status: String?
    ) {
        this.start = start
        this.finish = finish
        this.nameStart = nameStart
        this.nameFinish = nameFinish
        this.descProduct = descProduct
        this.executor = executor
        this.status = status
    }
}
