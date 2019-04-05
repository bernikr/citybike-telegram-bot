#!/bin/bash
set -e
scp build/libs/citybikes.jar citybikebot@euclid.srv.kralofsky.com:/var/citybikes/citybikes.jar
