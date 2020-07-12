package org.aarbizu.baseballDatabankFrontend

import com.xenomachina.argparser.ArgParser
import com.xenomachina.argparser.DefaultHelpFormatter
import com.xenomachina.argparser.default
import com.xenomachina.argparser.mainBody

class SimpleArgs(parser: ArgParser) {
    val foo by parser.flagging("-f", "--foo", help = "set flag foo")
    val bar by parser.storing("-b", "--bar", help = "set value for bar").default("baz")
}

fun main(args: Array<String>) = mainBody {
    ArgParser(
        args,
        helpFormatter = DefaultHelpFormatter(
            prologue = "[baseballDatabankFrontend]",
            epilogue = "Sample usage: baseballDatabankFrontend --foo --bar=swag"
        )
    ).parseInto(::SimpleArgs).run {
            if (foo) println("Foo is set.")
            println("bar is $bar.")
    }
    println("Hello, World!")
}
