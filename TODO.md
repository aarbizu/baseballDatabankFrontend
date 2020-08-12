### TODO

- [x] player name search (last name substring)
- [ ] refactor QueryRoutes to have individual form/query using an interface
- ~~[ ] (WIP) use the ktor param parsing to handle form submits, etc~~
    - [ ] actually KVars should be usable for all of this: keep tinkering
- [ ] add linking from bbrefid to the bbref site
- [x] add the ability to query player name by regex (first, last, full name)
- [ ] process the db to build some static content pages
    - [ ] players (reg/postseason)
    - [ ] teams (reg/postseason)
    - [ ] managers (reg/postseason)
- checkbox modifiers to superlative queries, e.g. top HR hitters whose names are:
    - [ ] ~~palindromes (infeasible, computationally)~~
    - [ ] supervocalic
    - [ ] stricy supervocalic (exactly one each of vowel)
- [ ] add player table column sort toggles. Don't re-query, just sort the result set and re-render
    