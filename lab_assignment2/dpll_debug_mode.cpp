#include <iostream>
#include <fstream>
#include <map>
#include <set> // set for fast checking pureLiterals
#include <string>
#include <vector>
#include <sstream> // string stream parsing
#include <algorithm> // std::count


void propagate (int l, std::map <int, std::vector < int>> &cnf,  std::map<int, std::string> &v){
	// if literal l is negative find -1 * l
	std::string assignedValue ;
	if( l < 0) assignedValue = v.find(-1*l) -> second;
	else assignedValue =  v.find(l) -> second;

	for ( auto clause : cnf){
		std::vector <int > newVec; 
		for( auto literal : clause.second){
			// atom l is in clause 
			if ( literal == -1 * l || literal == l ){
				if( (literal < 0 && assignedValue == "false") || (literal > 0 && assignedValue == "true")) {
					//std::cout << "key before erasing : " << clause.first<< std::endl;
					std::cout << "literal was " << literal << " and assignedValue was " << assignedValue << std:: endl; // ------> debug debug  debug  debug	 debug
					cnf.erase(clause.first);
				}	
				// else if( (literal > 0 && assignedValue == "false") || ( literal <0 && assignedValue == "true") ){
				// 	clause.second.erase( clause.second.begin() + i);
				// }
			}
			// if not in clause push all remainder 
			else newVec.push_back(literal);
		}
		auto itr = cnf.find(clause.first); 
		//std::cout << "trying to find key : " << clause.first << std::endl;
		if (itr != cnf.end()) {
			//std::cout << "linking vec" << std::endl;
			itr -> second = newVec;
		}
	}
}

void obviousAssign(int l, std::map<int, std::string> &v ){
	if( l < 0) v.find(-1* l) -> second = "false";
	else v.find(l) -> second = "true";
}

bool isPureLiteral(int l, std::map <int, std::vector < int>> cnf ){
	// loop through clauses 
	for ( auto element : cnf){
		// loop through each literal in the clause
		for ( auto literal : element.second){
			if ( literal * -1 == l) return false; 
		}
	}
	return true;
}
 
std::map<int, std::string> dpll(std::vector<int> atoms, std::map <int, std::vector < int>> cnf, std::map<int, std::string> v){

	// easy cases 
	//--------------------------------------------------------------------------------------------------------------------------
	bool easyCase = true;
	while ( easyCase){
		easyCase = false;
		
		// contain all pure literals of this iteration  
		std:: set <int>  pureLiterals;
		bool pureLiteralExists = false; 
		bool emptyClauseExists = false; 
		for ( auto clause : cnf){
			if ( (clause.second).empty()) emptyClauseExists = true;
			for (auto literal: clause.second){
				// if the set already contains the literal then skip  
				if( !pureLiterals.count(literal) &&  isPureLiteral(literal, cnf)){
					pureLiterals.insert(literal);
					pureLiteralExists = true;
				} 
			}
		}

		// all clauses are satisfied so assign any left over unbound as either true or false
		if ( cnf.empty()){
			for ( auto atoms : v){
				if ( atoms.second == "unbound") atoms.second = "true";
				std::cout << "inside #1 success case" << std::endl; // ---------------------------> debug debug  debug  debug	 debug
			}
			return v;
		}

		// if some clause in cnf is .empty() means unsatisfiable
		else if (emptyClauseExists){
			std::cout << "inside some empty vector exists" << std::endl; // ------------------------> debug debug  debug  debug	 debug
			std::cout << "--------------------------------" << std::endl; // ------------------------> debug debug  debug  debug	 debug
			std::map <int, std::string> nilMap = {{-1,"NIL"}};
			return nilMap;
		} 
		
		else if (pureLiteralExists){
			std::cout << "inside pure literal" << std::endl; // ------------------------> debug debug  debug  debug	 debug
			// if pureliterals existed then loop again 
			easyCase = true;
			// for all pure literals in the set 
			for ( auto eachPureLiteral : pureLiterals){
				obviousAssign(eachPureLiteral,v);
				// delete every clause that contains literal;
				// have to erase whole key --> vector pair 
				for ( auto clause : cnf){
					if( std::count ( clause.second.begin(),clause.second.end(),eachPureLiteral)){
						cnf.erase(clause.first);
					}
				}
			}
		}
		// if no other cases satisfy check for single literals 
		else {
			for ( auto clause : cnf){
				if (clause.second.size() == 1){
					//std::cout << "inside singleton" << std::endl; // ------------------------> debug debug  debug  debug	 debug
					// if single literals still exist loop again 
					easyCase = true; 
					
					int singleLiteral = clause.second[0];
					//std::cout << "single Literal is " <<  singleLiteral << std::endl; // ------------------------> debug debug  debug  debug	 debug
					obviousAssign(singleLiteral, v);
					propagate(singleLiteral, cnf, v);
					// -----------------------------------------------------------> debug debug  debug  debug	 debug
						// for (auto element : cnf){
						// std::cout << element.first << "--> [";
						// for ( auto l : element.second){
						// 	std::cout << l << " ";
						// }
						// std::cout << "]" << std::endl;
						// }
						// 	std::cout << "--------------------------------" << std::endl;
						// -----------------------------------------------------------> debug debug  debug  debug	 debug
					//std::cout << "after propagate and end of singleton" << std::endl; // ------------------------> debug debug  debug  debug	 debug
					break;
				}
			}
		}
		// -----------------------------------------------------------> debug debug  debug  debug	 debug
		for (auto element : cnf){
			std::cout << element.first << "--> [";
			for ( auto l : element.second){
				std::cout << l << " ";
			}
			std::cout << "]" << std::endl;
		}
		std::cout << "--------------------------------" << std::endl;
		// -----------------------------------------------------------> debug debug  debug  debug	 debug
	

	}// while loop


	// hard cases 
	//--------------------------------------------------------------------------------------------------------------------------
	int pickedAtom;
	for ( auto atom : v){
		if(atom.second == "unbound") {
			pickedAtom = atom.first;
			break;
		}	
	}
	
	v.find(pickedAtom) -> second = "true";
	std::cout << pickedAtom <<" picked for assigning true and actually assigned " << v.find(pickedAtom) -> second << std:: endl; // ------> debug debug  debug  debug	 debug
	std::map <int, std::vector < int>> cnfCopy = cnf;
	propagate(pickedAtom, cnfCopy, v);

	// -----------------------------------------------------------> debug debug  debug  debug	 debug
	for (auto element : cnfCopy){
		std::cout << element.first << "--> [";
		for ( auto l : element.second){
			std::cout << l << " ";
		}
		std::cout << "]" << std::endl;
	}
	std::cout << "--------------------------------" << std::endl;
	// -----------------------------------------------------------> debug debug  debug  debug	 debug	

	auto vNew = dpll(atoms,cnfCopy,v);
	// have an answer !!!
	if( vNew.find(-1) == vNew.end()) return vNew;
	else{
		v.find(pickedAtom) -> second = "false";
		std::cout << pickedAtom <<" picked for assigning false and actually assigned " << v.find(pickedAtom) -> second << std:: endl; // ------> debug debug  debug  debug	 debug
		propagate(pickedAtom, cnf, v);

		// -----------------------------------------------------------> debug debug  debug  debug	 debug
		for (auto element : cnf){
		std::cout << element.first << "--> [";
		for ( auto l : element.second){
			std::cout << l << " ";
		}
		std::cout << "]" << std::endl;
		}
			std::cout << "--------------------------------" << std::endl;
		// -----------------------------------------------------------> debug debug  debug  debug	 debug
		return dpll(atoms, cnf, v);
	}
}// end of dpll







int main (){

	//input 
	//-------------------------------------------------------------------

	std::vector <int> atoms; // 1 ~ 64 
	std::map <int, std::vector < int>> cnf; // vec [1 2 3 4 5 6 7 8] <-- one formula 
	std::map <int, std::string > v; // {atom , true/false/unbound}
	std::vector <std::string> keepCopy; // 1 A 1, 2 A 2 .... 9 B 1, 10 B 2 .... 64 H 8 


	std::ifstream inputFile("outputFront");	
	if( inputFile.is_open()){

		// input cnf formulas in to set cnf
		std::string line;
		int i = 0;
  		while ( getline (inputFile, line)) {
  			if ( line == "0") {
  				//std::cout << "break reached while reading cnf at line= " << line << std::endl;
  				break;
  			}

  			std::vector < int > oneFormula; 
  			std:: istringstream iss(line);
  			int token;
			while (iss >> token){
				oneFormula.push_back(token);
    		}
  			cnf.insert({i,oneFormula});
  			i ++;
		}
	}
	else {
		std::cerr << "Failed to open file" << std:: endl;
		exit(1);
	}

	// copy from 1 A 1, 2 A 2 .... 9 B 1
	std::string line;
	int numOfSymbols = 0;
	while ( getline (inputFile, line)) {
		numOfSymbols ++;
		// initialize v all to unbound 
		v.insert({numOfSymbols,"unbound"});
		// set of propositional atoms 
		atoms.push_back(numOfSymbols);
		// keep copy for writing to file later on 
		keepCopy.push_back(line);
	}	

	// numOfSymbols 64 from this point 
	inputFile.close();

	// -----------------------------------------------------------> debug debug  debug  debug	 debug
	std::cout << "----------main print----------------------" << std::endl;
	for (auto element : cnf){
		std::cout << element.first << "--> [";
		for ( auto l : element.second){
			std::cout << l << " ";
		}
		std::cout << "]" << std::endl;
	}
	std::cout << "--------------------------------" << std::endl;
	// -----------------------------------------------------------> debug debug  debug  debug	 debug

	v = dpll(atoms, cnf,v);

	std::cout << "--------------------------------" << std::endl;

	for (auto element : v){
		std::cout << element.first << "=" << element.second << std::endl;
	}

	std:: ofstream outputFile("outputDpll");

	return 0;
}
