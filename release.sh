#!/usr/bin/env sh

CHANGED=`git diff | grep "defproject stencil/stencil-core"`.empty?;
;
if CHANGED; then

lein clean && lein uberjar;


end
else
    echo "Not a release commit. Can not release"
    exit 1
    end
