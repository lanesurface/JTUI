REM NOTE: Cloc must be installed on your system and added to the `PATH`
REM       environment variable before this script can be run.
cloc .. --exclude-dir=docs --out=stats.txt --csv
