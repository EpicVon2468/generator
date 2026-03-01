package io.github.epicvon2468.generator

fun main(args: Array<String>) {
	Options.parse(args)
	println(Options.portable)
	println(Options.projectName)
	println(Options.outputDir)
	System.setProperty("generator.option.name", "foo")
	println(Options.projectName)
	println(Options.outputDir)
}