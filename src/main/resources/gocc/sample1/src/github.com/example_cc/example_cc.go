/*
Copyright IBM Corp. 2016 All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

		 http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package main

import (
	"fmt"
	"strconv"
	"strings"

	"github.com/hyperledger/fabric/core/chaincode/shim"
	pb "github.com/hyperledger/fabric/protos/peer"

	"encoding/json"
)

// SimpleChaincode example simple Chaincode implementation
type SimpleChaincode struct {
}

type ActivePoll struct{
	Name 		string 				`json:"name"`
	Token		int 				`json:"token"`
}

type Option struct{
	Name 		string 				`json:"name"`
	Count 		int 				`json:"count"`
}

type User struct{
	Active 		[]ActivePoll 		`json:"active"`
	Inactive	[]string 			`json:"inactive"`
}

type Poll struct{
	Options 		[]Option 		`json:"options"`
	Status 			int 			`json:"status"`
    Owner           string          `json:"owner"`
}

// Init initializes the chaincode state
func (t *SimpleChaincode) Init(stub shim.ChaincodeStubInterface) pb.Response {
	fmt.Println("########### example_cc Init ###########")
	_, args := stub.GetFunctionAndParameters()
	var A, B string    // Entities
	var user User 
	var userAsJsonByteArray []byte
    var userAsJsonString string
    var err error

	if len(args) != 2 {
		return shim.Error("Incorrect number of arguments. Expecting 4")
	}

	// Initialize the chaincode
	A = args[0]
	
	B = args[1]

	user, err = createUser()

	if err != nil {
		return shim.Error("Expecting integer value for asset holding")
	}

	userAsJsonByteArray, err = getUserAsJsonByteArray(user)
    if err != nil{
        return shim.Error(err.Error())
    }

    userAsJsonString = string(userAsJsonByteArray)
	
	err = stub.PutState(A, []byte(userAsJsonString))
	if err != nil {
		return shim.Error(err.Error())
	}
	err = stub.PutState(B, []byte(userAsJsonString))
	if err != nil {
		return shim.Error(err.Error())
	}


	fmt.Printf(A+" and "+B+" have been added to the ledger" )

	if transientMap, err := stub.GetTransient(); err == nil {
		if transientData, ok := transientMap["result"]; ok {
			return shim.Success(transientData)
		}
	}
	return shim.Success(nil)

}

// Invoke makes payment of X units from A to B
func (t *SimpleChaincode) Invoke(stub shim.ChaincodeStubInterface) pb.Response {
	fmt.Println("########### example_cc Invoke ###########")
	function, args := stub.GetFunctionAndParameters()

	if function != "invoke" {
		return shim.Error("Unknown function call")
	}

	if len(args) < 2 {
		return shim.Error("Incorrect number of arguments. Expecting at least 2")
	}

	if args[0] == "delete" {
		// Deletes an entity from its state
		return t.delete(stub, args)
	}

	if args[0] == "query" {
		// queries an entity state
		return t.query(stub, args)
	}
	if args[0] == "move" {
		// Deletes an entity from its state
		return t.move(stub, args)
	}
	if args[0] == "addUser" {
		return t.addUser(stub, args)
	}
	if args[0] == "newQuery" {
		return t.newQuery(stub, args)
	}
	if args[0] == "addNewActivePollToUser" {
		return t.addNewActivePollToUser(stub, args)
	}
	if args[0] == "activeToInactivePoll" {
		return t.activeToInactivePoll(stub, args)
	}
	if args[0] == "addNewPoll" {
		return t.addNewPoll(stub, args)
	}
	if args[0] == "vote" {
		return t.vote(stub, args)
	}
	if args[0] == "changeStatusToZero" {
		return t.changeStatusToZero(stub, args)
	}
	if args[0] == "addNewActivePollToManyUsers"{
        return t.addNewActivePollToManyUsers(stub,args)
    }
	return shim.Error("Unknown action, check the first argument, must be one of 'delete', 'query', or 'move'")
}

func (t *SimpleChaincode) move(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	// must be an invoke
	var A, B string    // Entities
	var Aval, Bval int // Asset holdings
	var X int          // Transaction value
	var err error

	if len(args) != 4 {
		return shim.Error("Incorrect number of arguments. Expecting 4, function followed by 2 names and 1 value")
	}

	A = args[1]
	B = args[2]

	// Get the state from the ledger
	// TODO: will be nice to have a GetAllState call to ledger
	Avalbytes, err := stub.GetState(A)
	if err != nil {
		return shim.Error("Failed to get state")
	}
	if Avalbytes == nil {
		return shim.Error("Entity not found")
	}
	Aval, _ = strconv.Atoi(string(Avalbytes))

	Bvalbytes, err := stub.GetState(B)
	if err != nil {
		return shim.Error("Failed to get state")
	}
	if Bvalbytes == nil {
		return shim.Error("Entity not found")
	}
	Bval, _ = strconv.Atoi(string(Bvalbytes))

	// Perform the execution
	X, err = strconv.Atoi(args[3])
	if err != nil {
		return shim.Error("Invalid transaction amount, expecting a integer value")
	}
	Aval = Aval - X
	Bval = Bval + X
	fmt.Printf("Aval = %d, Bval = %d\n", Aval, Bval)

	// Write the state back to the ledger
	err = stub.PutState(A, []byte(strconv.Itoa(Aval)))
	if err != nil {
		return shim.Error(err.Error())
	}

	err = stub.PutState(B, []byte(strconv.Itoa(Bval)))
	if err != nil {
		return shim.Error(err.Error())
	}

	if transientMap, err := stub.GetTransient(); err == nil {
		if transientData, ok := transientMap["result"]; ok {
			return shim.Success(transientData)
		}
	}
	return shim.Success(nil)
}

// Deletes an entity from state
func (t *SimpleChaincode) delete(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	if len(args) != 1 {
		return shim.Error("Incorrect number of arguments. Expecting 1")
	}

	A := args[1]

	// Delete the key from the state in ledger
	err := stub.DelState(A)
	if err != nil {
		return shim.Error("Failed to delete state")
	}

	return shim.Success(nil)
}

// Query callback representing the query of a chaincode
func (t *SimpleChaincode) query(stub shim.ChaincodeStubInterface, args []string) pb.Response {

	var A string // Entities
	var err error

	if len(args) != 2 {
		return shim.Error("Incorrect number of arguments. Expecting name of the person to query")
	}

	A = args[1]

	// Get the state from the ledger
	Avalbytes, err := stub.GetState(A)
	if err != nil {
		jsonResp := "{\"Error\":\"Failed to get state for " + A + "\"}"
		return shim.Error(jsonResp)
	}

	if Avalbytes == nil {
		jsonResp := "{\"Error\":\"Nil amount for " + A + "\"}"
		return shim.Error(jsonResp)
	}

	jsonResp := "{\"Name\":\"" + A + "\",\"Amount\":\"" + string(Avalbytes) + "\"}"
	fmt.Printf("Query Response:%s\n", jsonResp)
	return shim.Success(Avalbytes)
}

func main() {
	err := shim.Start(new(SimpleChaincode))
	if err != nil {
		fmt.Printf("Error starting Simple chaincode: %s", err)
	}
}

func (t *SimpleChaincode) addUser(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	var user User 
	var userAsJsonByteArray []byte
    var userAsJsonString string
    var userExists bool
    var err error

	if len(args) != 2 {
		return shim.Error("put operation must include one arguments, a key")
	}
	key := args[1]
	user, err = createUser()

	if err != nil {
		return shim.Error("Expecting integer value for asset holding")
	}

	userAsJsonByteArray, err = getUserAsJsonByteArray(user)
    if err != nil{
        return shim.Error(err.Error())
    }

    userAsJsonString = string(userAsJsonByteArray)
    userExists, err = isExistingUser(stub, key)
    if err != nil {
        return shim.Error(err.Error())
    }
    if userExists {
        return shim.Error("An asset already exists with id:" + key)
    }
	
	err = stub.PutState(key, []byte(userAsJsonString))
	if err != nil {
		return shim.Error(err.Error())
	}
	fmt.Println("New user: "+ key + " added to the ledger")

	return shim.Success(nil)
}

//peer chaincode query -C mychannel -n mycc -c '{"Args":["newQuery","c"]}'
//peer chaincode query -C mychannel -n mycc -c '{"Args":["newQuery","my_poll0"]}'
func (t *SimpleChaincode) newQuery(stub shim.ChaincodeStubInterface, args []string) pb.Response {

    var key string // Entities
    var err error
    var poll Poll
    var pollAsStringIfStatusOne string
    var pollAsByteIfStatusOne []byte

    if len(args) != 2 {
        return shim.Error("Incorrect number of arguments. Expecting name of the person to query")
    }
    key = args[1]

    // Get the state from the ledger
    pollAsBytes, err := stub.GetState(key)
    if err != nil {
        jsonResp := "{\"Error\":\"Failed to get state for " + key + "\"}"
        return shim.Error(jsonResp)

    }
    if pollAsBytes == nil {
        jsonResp := "{\"Error\":\"Nil amount for " + key + "\"}"
        return shim.Error(jsonResp)
    }
    poll, err = getPollFromJsonByteArray(pollAsBytes)
    if err != nil{
        return shim.Error(err.Error())
    }
    if(poll.Status==1){
        pollAsStringIfStatusOne = "{\"options\":["
        for i := 0; i < len(poll.Options); i++ {
            if i==0 {
                pollAsStringIfStatusOne = pollAsStringIfStatusOne + "{\"name\":\"" +poll.Options[i].Name+"\",\"count\":0}"
            } else {
                pollAsStringIfStatusOne = pollAsStringIfStatusOne + ",{\"name\":\"" +poll.Options[i].Name+"\",\"count\":0}"
            }
        }
        pollAsStringIfStatusOne = pollAsStringIfStatusOne + "],\"status\":1,\"owner\":\""+poll.Owner+"\"}"
        pollAsByteIfStatusOne = []byte(pollAsStringIfStatusOne)
        fmt.Printf("Query Response:%s\n", pollAsStringIfStatusOne)
        return shim.Success(pollAsByteIfStatusOne)
    }

    fmt.Printf("Query Response:%s\n", string(pollAsBytes))

    return shim.Success(pollAsBytes)
}

//peer chaincode invoke -o orderer.example.com:7050 -C mychannel -n mycc -c '{"Args":["addNewActivePollToUser","c","my_poll0"]}'
//peer chaincode invoke -o orderer.example.com:7050 -C mychannel -n mycc -c '{"Args":["addNewActivePollToUser","c","my_poll1"]}'
//peer chaincode invoke -o orderer.example.com:7050 -C mychannel -n mycc -c '{"Args":["addNewActivePollToUser","c","my_poll2"]}'
func (t *SimpleChaincode) addNewActivePollToUser(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	var user User 
	var userAsJsonByteArray []byte
    var userAsJsonString string
    var userExists, pollExists, pollAlreadyAddToUser bool
    var err error
    var pollToAdd ActivePoll

	if len(args) < 3 {
		return shim.Error("put operation must include one arguments, a key")
	}
	key := args[1]

    userExists, err = isExistingUser(stub, key)

    if err != nil {

        return shim.Error(err.Error())

    }

    if userExists==false {

        return shim.Error("No user with id:" + key)

    }

    pollExists, err = isExistingPoll(stub, args[2])
    if pollExists==false{
    	return shim.Error("No poll with name:" + args[2])
    }

	userAsBytes, err := stub.GetState(key)

    if err != nil{

        return shim.Error(err.Error())

    }

    user, err = getUserFromJsonByteArray(userAsBytes)
    if err != nil{
        return shim.Error(err.Error())
    }

    pollAlreadyAddToUser, err = isPollAlreadyAddToUser(stub, key, args[2])
    if pollAlreadyAddToUser==true {
    	return shim.Error("this poll: " + args[2] + " has already been add to the user: " +key)
    }

    pollToAdd,err = createActivePoll(args[2])
    if err != nil{
        return shim.Error(err.Error())
    }


    user.Active = append(user.Active,pollToAdd)

    userAsJsonByteArray, err = getUserAsJsonByteArray(user)

    if err != nil{

        return shim.Error(err.Error())

    }

    userAsJsonString = string(userAsJsonByteArray)

	err = stub.PutState(key, []byte(userAsJsonString))
	if err != nil {
		return shim.Error(err.Error())
	}

	return shim.Success(nil)
}

//peer chaincode invoke -o orderer.example.com:7050 -C mychannel -n mycc -c '{"Args":["addNewActivePollToManyUsers","c,d","my_poll0"]}'
func (t *SimpleChaincode) addNewActivePollToManyUsers(stub shim.ChaincodeStubInterface, args []string) pb.Response {
    var s string
    var pollKey string
    var key string
    var err error
    var user User 
    var userAsJsonByteArray []byte
    var userAsJsonString string
    var userExists, pollExists, pollAlreadyAddToUser bool
    var pollToAdd ActivePoll

    if len(args) < 3 {
        return shim.Error("put operation must include one arguments, a key")
    }
    s = args[1]
    pollKey = args[2]

    users := strings.Split(s, ",")

    pollExists, err = isExistingPoll(stub, pollKey)
    if pollExists==false{
        return shim.Error("No poll with name:" + pollKey)
    }

    pollToAdd,err = createActivePoll(pollKey)
    if err != nil{
        return shim.Error(err.Error())
    }

    for k := 0; k< len(users); k++{
        userExists, err = isExistingUser(stub, users[k])
        if err != nil {
            return shim.Error(err.Error())
        }
        if userExists==false {
            return shim.Error("No user with id:" + users[k])
        }
    }

    for i := 0; i < len(users); i++ {
        key = users[i]

        userAsBytes, err := stub.GetState(key)
        if err != nil{
            return shim.Error(err.Error())
        }
        user, err = getUserFromJsonByteArray(userAsBytes)
        if err != nil{
            return shim.Error(err.Error())
        }

        pollAlreadyAddToUser, err = isPollAlreadyAddToUser(stub, key, pollKey)
        if pollAlreadyAddToUser==true {
            return shim.Error("this poll: " + pollKey + " has already been add to the user: " +key)
        }

        user.Active = append(user.Active,pollToAdd)
        userAsJsonByteArray, err = getUserAsJsonByteArray(user)
        if err != nil{
            return shim.Error(err.Error())
        }
        userAsJsonString = string(userAsJsonByteArray)
        err = stub.PutState(key, []byte(userAsJsonString))
        if err != nil {
            return shim.Error(err.Error())
        }
    }

    return shim.Success(nil)
}

//peer chaincode invoke -o orderer.example.com:7050 -C mychannel -n mycc -c '{"Args":["activeToInactivePoll","c","my_poll0"]}'
func (t *SimpleChaincode) activeToInactivePoll(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	var user User 
	var userAsJsonByteArray []byte
    var userAsJsonString string
    var userExists, pollContained bool
    var err error
   	var pollName string
   	var indexOfThePoll int

	if len(args) < 3 {
		return shim.Error("put operation must include one arguments, a key")
	}
	key := args[1]
	pollName = args[2]

    userExists, err = isExistingUser(stub, key)

    if err != nil {

        return shim.Error(err.Error())

    }

    if userExists==false {

        return shim.Error("No user with id:" + key)

    }

	userAsBytes, err := stub.GetState(key)

    if err != nil{

        return shim.Error(err.Error())

    }
    if userAsBytes == nil {
		return shim.Error("Entity not found")
	}


    user, err = getUserFromJsonByteArray(userAsBytes)
	

    if err != nil{

        return shim.Error(err.Error())

    }

    pollContained = false
    indexOfThePoll = 0
    for i := 0; i < len(user.Active); i++ {
        if user.Active[i].Name==pollName {
        	indexOfThePoll = i
        	pollContained = true
        }
    }

    if pollContained==false {
    	return shim.Error("No such poll for this user")
    }

    user.Active = append(user.Active[:indexOfThePoll], user.Active[indexOfThePoll+1:]...)

    user.Inactive = append(user.Inactive,pollName)

    userAsJsonByteArray, err = getUserAsJsonByteArray(user)
    if err != nil{
        return shim.Error(err.Error())
    }
    userAsJsonString = string(userAsJsonByteArray)
	err = stub.PutState(key, []byte(userAsJsonString))
	if err != nil {
		return shim.Error(err.Error())
	}

	return shim.Success(nil)
}

//peer chaincode invoke -o orderer.example.com:7050 -C mychannel -n mycc -c '{"Args":["addNewPoll","my_poll0","{\"Options\":[{\"Name\":\"opt1\",\"Count\":0},{\"Name\":\"opt2\",\"Count\":0}],\"status\":1}"]}'
func (t *SimpleChaincode) addNewPoll(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	var poll Poll
	var pollAsJsonByteArray []byte
    var pollAsJsonString string
	var ownerExists, pollExists bool
	var err error
	var owner string

	if len(args) < 4 {
		return shim.Error("put operation must include one arguments, a key")
	}

	key := args[1]
	s := args[2]
	owner = args[3]

	ownerExists, err = isExistingUser(stub, owner)
    if err != nil {
        return shim.Error(err.Error())
    }
    if ownerExists==false {
        return shim.Error("No user with id:" + owner)
    }

	poll, err = createNewPoll(s)
	if err != nil {
		return shim.Error("error during the createNewPoll function")
	}

	pollAsJsonByteArray, err = getPollAsJsonByteArray(poll)
    if err != nil{
        return shim.Error(err.Error())
    }

    pollAsJsonString = string(pollAsJsonByteArray)
    pollExists, err = isExistingPoll(stub, key)
    if err != nil {
        return shim.Error(err.Error())
    }
    if pollExists {
        return shim.Error("A poll already exists with id:" + key)
    }
	

	err = stub.PutState(key, []byte(pollAsJsonString))
	if err != nil {
		return shim.Error(err.Error())
	}
	fmt.Println("New poll: "+ key + " has been added to the ledger")
	
	return shim.Success(nil)
}

//peer chaincode invoke -o orderer.example.com:7050 -C mychannel -n mycc -c '{"Args":["vote","c","my_poll0","opt1"]}'
func (t *SimpleChaincode) vote(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	var userAsJsonBytes, pollAsJsonBytes []byte
	var userAsJsonString, pollAsJsonString string
	var UserAllowed, userExists, pollExists, optionExists, statusOne bool
	var pollKey, userKey, option string
	var err error
	var user User
	var poll Poll
	var indexOfThePoll, indexOfTheOption int

	if len(args) < 3 {
		return shim.Error("put operation must include three arguments, a userKey, a pollKey and an option ")
	}

	userKey = args[1]
	pollKey = args[2]
	option = args[3]


	userExists, err = isExistingUser(stub, userKey)
    if err != nil {
        return shim.Error(err.Error())
    }
    if userExists==false {
        return shim.Error("No user with id:" + userKey)
    }

    pollExists, err = isExistingPoll(stub, pollKey)
    if err != nil {
        return shim.Error(err.Error())
    }
    if pollExists==false {
        return shim.Error("No poll named: " + pollKey + " has been created")
    }

    UserAllowed, indexOfThePoll, err = isUserAllowed(stub,userKey,pollKey)
    if UserAllowed==false {
    	return shim.Error("The user: " + userKey + " is not allowed to participate to this poll")
    }

    optionExists, indexOfTheOption, err = isExistingOption(stub,pollKey,option)
    if optionExists==false {
    	return shim.Error("The option: " + option + " does not exist")
    }

    userAsJsonBytes, err = stub.GetState(userKey)
    if err != nil{
        return shim.Error(err.Error())
    }
    if userAsJsonBytes == nil {
		return shim.Error("Entity not found")
	}
    user, err = getUserFromJsonByteArray(userAsJsonBytes)
	if err != nil{
        return shim.Error(err.Error())
    }

    pollAsJsonBytes, err = stub.GetState(pollKey)
    if err != nil{
        return shim.Error(err.Error())
    }
    if pollAsJsonBytes == nil {
		return shim.Error("Entity not found")
	}
    poll, err = getPollFromJsonByteArray(pollAsJsonBytes)
	if err != nil{
        return shim.Error(err.Error())
    }

    statusOne, err = isStatusOne(stub, poll)
    if statusOne == false {
    	return shim.Error("the current poll is closed")
    }

    user.Active[indexOfThePoll].Token = user.Active[indexOfThePoll].Token - 1
    poll.Options[indexOfTheOption].Count = poll.Options[indexOfTheOption].Count + 1

    userAsJsonBytes, err = getUserAsJsonByteArray(user)
    if err != nil{
        return shim.Error(err.Error())
    }
    userAsJsonString = string(userAsJsonBytes)
    err = stub.PutState(userKey, []byte(userAsJsonString))
	if err != nil {
		return shim.Error(err.Error())
	}

	pollAsJsonBytes, err = getPollAsJsonByteArray(poll)
	if err != nil{
        return shim.Error(err.Error())
    }
    pollAsJsonString = string(pollAsJsonBytes)
	err = stub.PutState(pollKey, []byte(pollAsJsonString))
	if err != nil {
		return shim.Error(err.Error())
	}

	return shim.Success(nil)
}
//peer chaincode invoke -o orderer.example.com:7050 -C mychannel -n mycc -c '{"Args":["changeStatusToZero","my_poll0"]}'
func (t *SimpleChaincode) changeStatusToZero(stub shim.ChaincodeStubInterface, args []string) pb.Response{
	var pollKey, pollAsJsonString string
	var pollAsBytes []byte
	var poll Poll
	var statusOne, ownerAllowed  bool
	var err error
    var currentUser string

	if len(args) != 3 {
		return shim.Error("put operation must include three arguments, a userKey, a pollKey and an option ")
	}
	
	pollKey = args[1]
	currentUser = args[1]

    ownerAllowed, err = isOwnerAllowed(stub,pollKey,currentUser)
    if ownerAllowed == false {
        return shim.Error("the user: "+ currentUser + " is not the owner of the poll")
    }

	pollAsBytes, err = stub.GetState(pollKey)
    poll, err = getPollFromJsonByteArray(pollAsBytes)

    statusOne, err = isStatusOne(stub, poll)
    if statusOne == false{
    	return shim.Error("The status of the poll: "+ pollKey +" has already been changed")
    }

    poll.Status = poll.Status - 1

    pollAsBytes, err = getPollAsJsonByteArray(poll)
	if err != nil{
        return shim.Error(err.Error())
    }
    pollAsJsonString = string(pollAsBytes)
	err = stub.PutState(pollKey, []byte(pollAsJsonString))
	if err != nil {
		return shim.Error(err.Error())
	}

	return shim.Success(nil)
}

func createUser() (User, error){
	emptyActive := []ActivePoll{}
	emptyInactive := []string{}
	var err error

    return User{Active: emptyActive, Inactive: emptyInactive}, err
}

func createActivePoll(pollName string) (ActivePoll, error){
	tok := 1
	var err error

    return ActivePoll{Name: pollName, Token: tok}, err
}

func createNewPoll(s string) (Poll, error){
	var poll Poll
	var err error
	err = json.Unmarshal([]byte(s), &poll)

	return poll,err

}

func isExistingUser(stub shim.ChaincodeStubInterface, key string) (bool, error){

    var err error

    result := false

    userAsBytes, err := stub.GetState(key)

    if(len(userAsBytes) != 0){

        result = true

    }

    return result, err
}

func isExistingPoll(stub shim.ChaincodeStubInterface, key string) (bool, error){

    var err error

    result := false

    pollAsBytes, err := stub.GetState(key)

    if(len(pollAsBytes) != 0){

        result = true

    }

    return result, err
}

func isUserAllowed(stub shim.ChaincodeStubInterface, userKey string, pollKey string) (bool, int, error){

    var err error
    index :=0
    result := false

    userAsBytes, err := stub.GetState(userKey)

    user, err := getUserFromJsonByteArray(userAsBytes)

    for i := 0; i < len(user.Active); i++ {
        if user.Active[i].Name==pollKey {
        	if user.Active[i].Token >0{
	        	result = true
	        	index = i
	        }
        }
    }

    return result, index, err
}

func isPollAlreadyAddToUser(stub shim.ChaincodeStubInterface, userKey string, pollKey string) (bool, error){

    var err error
    result := false

    userAsBytes, err := stub.GetState(userKey)

    user, err := getUserFromJsonByteArray(userAsBytes)

    for i := 0; i < len(user.Active); i++ {
        if user.Active[i].Name==pollKey {
        	if user.Active[i].Token >0{
	        	result = true
	        }
        }
    }
    for i := 0; i < len(user.Inactive); i++ {
        if user.Inactive[i]==pollKey {
	        result = true
        }
    }
    return result, err
}

func isExistingOption(stub shim.ChaincodeStubInterface, pollKey string, option string) (bool, int, error){

    var err error
    indexOfTheOption := 0
    result := false
    pollAsBytes, err := stub.GetState(pollKey)
    poll, err := getPollFromJsonByteArray(pollAsBytes)

    for i := 0; i < len(poll.Options); i++ {
        if poll.Options[i].Name==option {
        	result = true
        	indexOfTheOption=i
        }
    }

    return result,indexOfTheOption, err
}

func isStatusOne(stub shim.ChaincodeStubInterface, poll Poll) (bool,error){
	var err error
	var result bool

	result = false
    if poll.Status == 1 {
    	result = true
    }

    return result, err
}

func isOwnerAllowed(stub shim.ChaincodeStubInterface, pollKey string, currentUser string) (bool,error){
    var err error
    result := false
    pollAsBytes, err := stub.GetState(pollKey)
    poll, err := getPollFromJsonByteArray(pollAsBytes)
    if poll.Owner ==currentUser{
        result = true
    }
    return result,err
}

func getUserAsJsonByteArray(user User) ([]byte, error){

    var jsonUser []byte

    var err error

    jsonUser, err = json.Marshal(user)

    return jsonUser, err

}

func getUserFromJsonByteArray(userAsJsonByte []byte) (User, error) {

    var user User

    var err error

    err = json.Unmarshal(userAsJsonByte, &user)

    return user, err

}

func getPollAsJsonByteArray(poll Poll) ([]byte, error){

    var jsonPoll []byte

    var err error

    jsonPoll, err = json.Marshal(poll)

    return jsonPoll, err

}


func getPollFromJsonByteArray(pollAsJsonByte []byte) (Poll, error){
    var poll Poll
    var err error

    err = json.Unmarshal(pollAsJsonByte, &poll)
    return poll, err
}
