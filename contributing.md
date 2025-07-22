# Contributing to CrazyAuuctions
Contributions to the project are always welcome, Pull Requests do have some guidelines before being approved.

## You should always create the fork as a personal repository not in an organization.
Any pull request made by a fork in an organization prevents modifications. Everyone has their own way of doing things and rather asking you to change that. A personal fork lets us change the things
that we have a tick about. 

If you do not use a personal fork, We have to manually merge your pull request which means it's marked as closed instead of merged.

## Requirements
* `git`
* Java 21 ( Adoptium or Corretto is recommended )

### Pull Requests must be labeled properly according to if it's a bug fix, a new feature or enhancements to the code base.
* `git checkout -b fix/your_fix`
* `git checkout -b feature/your_feature`
* `git checkout -b quality/your_enhancement`
* Commit your changes using `git commit -m 'your commit'`
* Push to your branch using `git push`

### What's next?
* The branch you open the pull request to depends on the scope of your change.
  * Anything considered a "fix" or "internal" clean up that will not cripple a server or vastly change the experience of a server can be sent as a pull request to the `main` branch
    * We try to follow semver so once the pull request is deemed ready to merge, The version will be bumped from 1.0.0 -> 1.0.1 as an example.

You must explain what your pull request is changing and if needed, Supply a video of your change as Pull Requests are a way to get feedback.

## Api Additions
N/A