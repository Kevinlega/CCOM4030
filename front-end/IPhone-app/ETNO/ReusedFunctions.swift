//
//  ReusedFunctions.swift
//  ETNO
//
//  Created by Kevin Legarreta on 10/14/18.
//  Copyright © 2018 Los 5. All rights reserved.
//

import Foundation
import UIKit

// MARK: - Password Handlers
// Self explanatory, returns a salted and hashed password
public func saltAndHash(password: String, salt: String) -> String{
    let hashedPassword = password + salt
    return String(hashedPassword.hash)
}

// Generates salt for password
public func saltGenerator(length: Int,initialValue: UInt) -> String{
    let characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTXUVXYZ0123456789"
    let key = (0..<length).compactMap{_ in characters.randomElement()}
    var  salt = String(key)
    salt = LFSR(data: salt, initialValue: initialValue)
    return salt;
}

// MARK: - Alert Function

public func Alert(title: String, message: String, Dismiss: String) -> UIAlertController{
    let alertController = UIAlertController(title: title, message: message, preferredStyle: UIAlertController.Style.alert)
    alertController.addAction(UIAlertAction.init(title: Dismiss, style: UIAlertAction.Style.destructive, handler: {(alert: UIAlertAction!) in print("Bad")}))
    
    return alertController
}

// MARK: - Get the Last Bit
public func theLowestBit(newState: UInt) -> Character{
    let lastBit = String(newState,radix: 2)
    return lastBit.last!
}

// MARK: - Change the InitialValue
public func feedback(initialValue: UInt) -> UInt{
    var newState = initialValue
    
    for _ in 1...10{
        if theLowestBit(newState: newState) == "0"{
            newState = newState>>1
        }
        else{
            newState = (newState>>1) ^ 0x85913829
        }
    }
    return newState
}

// MARK: - LFSR Algorithm
public func LFSR(data: String, initialValue: UInt)-> String {

    var newState = initialValue
    var hexadecimal : String
    var output = ""
    
    for letter in data {
        newState = feedback(initialValue: newState)
        let char = String(letter).unicodeScalars
        let charInt = UInt(char[char.startIndex].value)
        hexadecimal = String(newState ^ (charInt & UInt(0xFF)))
        output.append(hexadecimal)
    }
    return output
}

// MARK: - Generate Random InitialValue for LFSR
public func generateRandomUInt() -> UInt{
    return UInt.random(in: 0..<4294967295)
}

// MARK: - Connect to API
public func ConnectToAPI(request: URLRequest) -> NSDictionary{
    
    var json : NSDictionary = NSDictionary()
    let group = DispatchGroup()
    group.enter()
    
    let task = URLSession.shared.dataTask(with: request) { (data: Data?, response: URLResponse?, error: Error?) in
        do{
            json = try! JSONSerialization.jsonObject(with: data!, options: .allowFragments) as! NSDictionary
            group.leave()
            }
        }
    task.resume()
    group.wait()
    return json
}
