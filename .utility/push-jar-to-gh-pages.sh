#!/bin/bash
set -ev # Exit with nonzero exit code if anything fails (mostly taken from swt-bling/readytalk on github

if [ "$TRAVIS_REPO_SLUG" == "onzfonz/Fractions-Card-Game" ] && [ "$TRAVIS_PULL_REQUEST" == "false" ] && [ "$TRAVIS_BRANCH" == "2012" ]; then

  echo -e "Publishing jar file...\n"

  cp -R build/libs/ $HOME/jar-latest

  cd $HOME
  git config --global user.email "travis@travis-ci.org"
  git config --global user.name "travis-ci"
  git clone --quiet --branch=gh-pages https://${GH_TOKEN}@github.com/onzfonz/Fractions-Card-Game gh-pages > /dev/null

  cd gh-pages
  git rm -rf ./jar
  cp -Rf $HOME/jar-latest ./jar
  git add -f .
  git commit -m "Latest jar on successful travis build $TRAVIS_BUILD_NUMBER auto-pushed to gh-pages"
  git push -fq origin gh-pages > /dev/null

  echo -e "Published Jar to gh-pages.\n"
  
fi