nCrEdges = 96; % only one true preference 

crEdges = [| pMaib, pSiem | pSiem, pMsg | pMsg, pSap | pSap, pOracle
           | schrOracle, schrMsg | schrMsg, schrMaib 
           | weCapg, weZuehlke | weZuehlke, weSap | weSap, weSiem | weSiem, weOracle | weOracle, weMsg | weMsg, weMaib
           | cimSap, cimSiem | cimSiem, cimMaib
           | engZuehlke, engCap | engCap, engMaib
           | bruSiem, bruMaib | bruMaib, bruCap
           | riSiem, riZuehlke | riZuehlke, riCapg | riCapg, riMsg | riMsg, riSap | riSap, riMaib | riMaib, riOracle
           | kinOracle, kinSap | kinSap, kinSiem
           | woeZuehlke, woeCapg | woeCapg, woeOracle | woeOracle, woeSap | woeSap, woeMsg | woeMsg, woeMaib
           | schmMaib, schmSap | schmSap, schmOracle | schmOracle, schmSiem
           | schnBad, schnOK | schnOK, schnFavs
           | harZuehlke, harOracle | harOracle, harCapg | harCapg, harSap | harSap, harMsg | harMsg, harSiem | harSiem, harMaib
           | vogCapg, vogSap | vogSap, vogMaib | vogMaib, vogZuehlke | vogZuehlke, vogSiem | vogSiem, vogOracle | vogOracle, vogMsg
           | wurZuehlke, wurSap | wurSap, wurOracle | wurOracle, wurCapg | wurCapg, wurMsg | wurMsg, wurSiem | wurSiem, wurMaib
           | friOracle, friSap | friSap, friCapg | friCapg, friMsg | friMsg, friSiem | friSiem, friZuehlke | friZuehlke, friMaib
           | jenSap, jenOracle
% ------------------------- Companies ---------------------
           | maibEng, maibSchn | maibSchn, maibCim | maibCim, maibWoe | maibWoe, maibJen | maibJen, maibKind 
           | oracWoe, oracPers | oracPers, oracVog | oracVog, oracWeb | oracWeb, oracSchm | oracSchm, oracKind | oracKind, oracRie | oracRie, oracJen
           | capGKind, capGSchnapp | capGKind, capGEng | capGKind, capGJen 
           | capGPers, capGSchnapp | capGPers, capGEng | capGPers, capGJen   
           | capGWurz , capGSchnapp | capGWurz , capGEng | capGWurz, capGJen  
           | sapCim, sapRie | sapRie, sapSchnapp | sapSchnapp, sapVog | sapVog, sapKind | sapKind, sapWoe | sapWoe, sapJen
           | sieWur, sieVog | sieVog, sieSchnapp | sieSchnapp, sieRie | sieRie, sieKind | sieKind, sieFri | sieFri, sieEng 
      %     | kinOracle, kinSap | kinSap, kinSiem
           | zueWeb, zueSchm | zueSchm, zueJen 
% -------------------------- Dummies ----------------------
 |];
