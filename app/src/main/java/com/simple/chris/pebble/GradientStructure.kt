package com.simple.chris.pebble

class GradientStructure {
    lateinit var gradientName: String
    lateinit var startColour: String
    lateinit var endColour: String
    lateinit var description: String
    lateinit var creatorsName: String
    var likeCount: Int = 0

    constructor(){}

    constructor(name: String, start: String, end: String, desc: String, creator: String, likes: Int) {
        this.gradientName = name
        this.startColour = start
        this.endColour = end
        this.description = desc
        this.creatorsName = creator
        this.likeCount = likes
    }
}