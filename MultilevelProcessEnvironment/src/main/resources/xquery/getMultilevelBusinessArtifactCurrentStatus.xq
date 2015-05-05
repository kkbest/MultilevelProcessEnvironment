import module namespace mba = 'http://www.dke.jku.at/MBA';
import module namespace sc  = 'http://www.w3.org/2005/07/scxml';

declare variable $mba external;

let $configuration := mba:getConfiguration($mba)

for $state in $configuration
  return fn:string($state/@id)
