package io.github.epicvon2468.generator

import kotlin.reflect.KProperty

data object Options {

	@JvmStatic
	fun parse(args: Array<String>): Unit = args.forEach { arg: String ->
		val (key: String, value: String) = parseArg(arg.dropWhile('-'::equals))
		System.setProperty("generator.option.$key", value)
	}

	private fun parseArg(arg: String): Pair<String, String> {
		val split: Int = arg.indexOf(':')
		if (split == -1) return arg to "true"
		return arg.substring(0..<split) to arg.substring((split + 1)..<arg.length)
	}

	operator fun getValue(`class`: Options, property: KProperty<*>): String? = System.getProperty("generator.option.${property.name}")
}