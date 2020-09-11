### TODO

- [x] player name search (last name substring)
- [x] refactor QueryRoutes to have individual form/query using an interface
- ~~[ ] (WIP) use the ktor param parsing to handle form submits, etc~~
    - [ ] actually KVars should be usable for all of this: keep tinkering
- [ ] add tests
    - [ ] play around with mockK for mocking
    - [ ] review [kotlin utest best practices](https://phauer.com/2018/best-practices-unit-testing-kotlin/#change-the-lifecycle-default-for-every-test-class)
- [ ] add linking from bbrefid to the bbref site
- [x] add the ability to query player name by regex (first, last, full name)
- [ ] process the db to build some static content pages
    - [ ] players (reg/postseason)
    - [ ] teams (reg/postseason)
    - [ ] managers (reg/postseason)
- checkbox modifiers to superlative queries, e.g., top HR hitters whose names are:
    - [ ] ~~palindromes (infeasible, computationally)~~
    - [ ] supervocalic
    - [ ] strict supervocalic (exactly one each of vowel)
- [ ] add player table column sort toggles. Don't re-query, just sort the result set and re-render
- [ ] top-level menu: add hover text over each item to have a description/example of usage
- [ ] work on deployment code
    - [ ] use gitpages? heroku?
    