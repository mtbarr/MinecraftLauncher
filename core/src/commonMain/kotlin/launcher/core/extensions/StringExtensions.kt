package launcher.core.extensions

import launcher.core.fileSeparator

infix fun String.withSeparator(other: String): String = this + fileSeparator() + other
