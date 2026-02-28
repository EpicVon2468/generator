package io.github.epicvon2468.generator

import java.io.File

import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

data object Options {

	val projectName: String by Option.string(
		name = "name",
		default = { "example" }
	)
	val outputDir: File by Option.file(
		name = "out",
		default = ::projectName
	)

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
	val default: () -> String,
	val resolver: (String) -> V
): ReadOnlyProperty<Any?, V> {

	private var cachedProp: String? = null
	private var cachedValue: V? = null

	override fun getValue(thisRef: Any?, property: KProperty<*>): V {
		val prop: String = System.getProperty("generator.option.$name", default())
		if (cachedProp == prop) return cachedValue!!
		return resolver(prop).also { value: V ->
			cachedProp = prop
			cachedValue = value
		}
	}

	companion object {

		@JvmStatic
		fun string(
			name: String,
			default: () -> String,
			resolver: (String) -> String = { it }
		): Option<String> = Option(name, default, resolver)

		@JvmStatic
		fun file(
			name: String,
			default: () -> String,
			resolver: (String) -> File = { File(it).absoluteFile }
		): Option<File> = Option(name, default, resolver)
	}
}