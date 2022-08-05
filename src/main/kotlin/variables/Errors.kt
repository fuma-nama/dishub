package variables

import utils.buildError

val NO_GUILD = buildError("You can only call this command in a Guild")
val MISSING_PERMISSIONS = buildError("Please give me the Administrator Permission")