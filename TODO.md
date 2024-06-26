### TODO

- [x] player name search (last name substring)
- ~~[ ] refactor QueryRoutes to have individual form/query using an interface~~
- ~~[ ] (WIP) use the ktor param parsing to handle form submits, etc~~
    - ~~[ ] actually KVars should be usable for all of this: keep tinkering~~
    - [x] just go with the <String,KVar> map instead of Parameters in route dispatch
- [x] add tests
    - [x] play around with mockK for mocking
    - [x] review [kotlin utest best practices](https://phauer.com/2018/best-practices-unit-testing-kotlin/#change-the-lifecycle-default-for-every-test-class)
- [x] add linking from bbrefid to the bbref site
- [x] add the ability to query player name by regex (first, last, full name)
    - [ ] rework the uri pattern, maybe use mulitiple '/' segs, i.e. "/regex/fname.pattern/lname.pattern/case.sense/et al."
- [ ] process the db to build some static content pages (not sure I want to try to recreate bbref, though)
    - [ ] players (reg/postseason)
    - [ ] teams (reg/postseason)
    - [ ] managers (reg/postseason)
- checkbox filters to superlative queries, e.g., top HR hitters whose names are:
    - [ ] palindromes (infeasible, computationally via regex... but maybe doable with string ops)
    - [x] supervocalic
    - [x] strict supervocalic (exactly one each of vowel)
- [ ] basic leaderboards for counting stats 
    - [ ] apply checkbox modifiers, eventually
- [ ] basic leaderboards for career rate stats 
    - [ ] filterable, as above
- [ ] add player table column sort toggles. Don't re-query, just sort the result set and re-render
- [ ] top-level menu: add hover text over each item to have a description/example of usage
- ~~[x] work on deployment code~~
    - ~~[ ] use gitpages? heroku?~~
- [x] fix display of top-n buttons with fontawesome or something for up/down arrows
- [ ]