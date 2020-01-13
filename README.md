# tic80downloader
Cart downloader and helper for TIC-80

This script download all carts from https://tic.computer/play for the Tic-80 Fantasy Console.
Since the server serve all carts as "cart.tic", the program rename the file with a custom name based on the game name (removes space and non alphanumeric characters).

It also produces a [TSV](https://en.wikipedia.org/wiki/Tab-separated_values) file (tic80.tsv) with some useful information for each game, such as:

* id
* hash
* name
* author
* fileName (as estracted by this program)
* crc32 of file (useful for libretro playlist)
* url of cart

Launching
===

You can launch it as script with any recent version of Java like this:

`java tic80downloader.java`

All files will be produced in the same folder.
