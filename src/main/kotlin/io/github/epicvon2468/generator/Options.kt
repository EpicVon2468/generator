package io.github.epicvon2468.generator

import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

data object Options {

	val projectName: String by Option.identity("name", "example")

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
}

data class Option<V>(
	val name: String,
	val default: String,
	val resolver: (String) -> V
): ReadOnlyProperty<Any?, V> {

	private var cachedProp: String? = null
	private var cachedValue: V? = null

	override fun getValue(thisRef: Any?, property: KProperty<*>): V {
		val prop: String = System.getProperty("generator.option.$name", default)
		if (cachedProp == prop) return cachedValue!!
		return resolver(prop).also { value: V ->
			cachedProp = prop
			cachedValue = value
		}
	}

	companion object {

		@JvmStatic
		fun identity(name: String, default: String): Option<String> = Option(name, default) { it }
	}
}