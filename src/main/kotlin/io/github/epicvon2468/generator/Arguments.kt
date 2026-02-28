package io.github.epicvon2468.generator

data object Arguments {

	private lateinit var _args: Set<Argument>

	fun parse(args: Array<String>) {
		_args = args.map { arg: String -> parseArg(arg.dropWhile { it == '-' }) }.toSet()
		println(_args)
	}

	private fun parseArg(arg: String): Argument {
		val split: Int = arg.indexOf(':')
		if (split == -1) return arg to "true"
		return arg.substring(0..<split) to arg.substring((split + 1)..<arg.length)
	}
}

typealias Argument = Pair<String, String>