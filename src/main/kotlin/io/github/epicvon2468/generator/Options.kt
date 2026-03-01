package io.github.epicvon2468.generator

import java.io.File

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

data object Options {

	var projectName: String by Option.string(
		name = "name",
		default = { "example" }
	)
	val outputDir: File by Option.file(
		name = "out",
		default = ::projectName
	)
	@get:JvmName("isPortable")
	val portable: Boolean by Option.boolean(
		name = "portable",
		default = { "false" }
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
	val serialiser: (String) -> V,
	val deserialiser: (V) -> String = { it.toString() }
): ReadWriteProperty<Any?, V> {

	@JvmField
	val key: String = "generator.option.$name"

	private var cachedProp: String? = null
	private var cachedValue: V? = null

	override fun getValue(thisRef: Any?, property: KProperty<*>): V {
		val prop: String = System.getProperty(key, default())
		if (cachedProp == prop) return cachedValue!!
		return serialiser(prop).also { value: V ->
			cachedProp = prop
			cachedValue = value
		}
	}

	override fun setValue(thisRef: Any?, property: KProperty<*>, value: V) {
		if (value == null) {
			System.clearProperty(key)
			return
		}
		System.setProperty(key, deserialiser(value))
	}

	companion object {

		@JvmStatic
		fun string(
			name: String,
			default: () -> String,
			serialiser: (String) -> String = { it },
			deserialiser: (String) -> String = { it }
		): Option<String> = Option(name, default, serialiser, deserialiser)

		@JvmStatic
		fun file(
			name: String,
			default: () -> String,
			serialiser: (String) -> File = { File(it).absoluteFile },
			deserialiser: (File) -> String = { it.absolutePath }
		): Option<File> = Option(name, default, serialiser, deserialiser)

		@JvmStatic
		fun boolean(
			name: String,
			default: () -> String,
			serialiser: (String) -> Boolean = { it.toBooleanStrict() },
			deserialiser: (Boolean) -> String = { it.toString() }
		): Option<Boolean> = Option(name, default, serialiser, deserialiser)
	}
}