#!/bin/bash
set -e
git config --global push.default simple
git remote add deploy citybikebot@euclid.srv.kralofsky.com:citybike-telegram-bot
git push deploy master
