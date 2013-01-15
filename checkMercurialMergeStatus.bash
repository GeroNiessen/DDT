#!/bin/bash

#Command Line Parameters: current|branchName\{none=all}  
allBranches=$(hg branches | awk -F' ' '{print $1}')

if [ -z "$1"  ]
then 
  branchesToCheck=$allBranches
else
  if [ $1 == "current" ]
  then
    branchesToCheck=$(hg branch)
  else
    checkBranchExistence=$(echo ${allBranches[*]} | grep -c "${1}")
    if [ $checkBranchExistence -gt 0 ]
    then
      branchesToCheck=${1}
    else
      echo -e -n "!!! Branch ${1} does not exist! Checking all branches instead! !!!\n"
      branchesToCheck=$allBranches
    fi
  fi
fi

for currentBranch in $branchesToCheck
do
  currentBranchParents=$(hg log --branch ${currentBranch} | grep parent | awk -F' ' '{print $2}')
  echo -e -n "\n======== Checking Branch ${currentBranch} =========\n"
  for currentOtherBranch in $allBranches
  do
    if [ $currentOtherBranch != $currentBranch ]
    then
      latestVersionOfOtherBranch=$(hg head ${currentOtherBranch} | grep changeset | awk -F' ' '{print $2}')
      currentBranchParentContainsLatestVersionOfOtherBranch=$(echo ${currentBranchParents[*]} | grep -c ${latestVersionOfOtherBranch})
      if [ $currentBranchParentContainsLatestVersionOfOtherBranch -gt 0 ]
      then
        echo -e -n "\tLATEST  VERSION  of Branch \"${currentOtherBranch}\" included!\n"
      else
        echo -e -n "\tUNKNOWN VERSION  of Branch \"${currentOtherBranch}\" included!\n"
      fi
    fi
  done
done