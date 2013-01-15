#!/bin/bash
latestVersionOfA=$(hg head ${2} | grep changeset | awk -F' ' '{print $2}')
echo "Latest Version of branch ${2}:        ${latestVersionOfA}"
versionOfAInBranchB=$(hg log --branch ${1} | grep parent | grep ${latestVersionOfA} | awk -F' ' '{print $2}')
echo "Branch ${1} contains ${2} in version: ${versionOfAInBranchB}"
if [ "$latestVersionOfA" = "$versionOfAInBranchB" ]
then
    echo "Branch develop is up to date!"
else
    echo "Please merge branch ${2} into branch ${1}"
fi

