http://vos.openlinksw.com/owiki/wiki/VOS/VirtTipsAndTricksGuideDeleteTriplesWithBlanknodes

  SELECT  (<LONG::bif:iri_id_num>(?s)) AS ?s_num ,
                                          ?p     ,
                                          ?o 
  FROM <http://sample/> 
  WHERE 
    { 
      ?s  ?p  ?o 
    };

