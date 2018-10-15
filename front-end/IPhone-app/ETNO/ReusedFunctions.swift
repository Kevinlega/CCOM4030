//
//  ReusedFunctions.swift
//  ETNO
//
//  Created by Kevin Legarreta on 10/14/18.
//  Copyright © 2018 Los 5. All rights reserved.
//

import Foundation
import UIKit
import CommonCrypto

// MARK: - Password Handlers

// Self explanatory, returns a salted and hashed password
func saltAndHash(password: String, salt: String) -> String{
    let hashedPassword = password + salt;
    return String(hashedPassword.hash)
}

// Generates salt for password
func saltGenerator(length: Int) -> String{
    let characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTXUVXYZ0123456789";
    let key = (0..<length).compactMap{_ in characters.randomElement()};
    let salt = String(key);
    return salt;
}

// MARK: - Alert Function

func Alert(title: String, message: String, Dismiss: String) -> UIAlertController{
    let alertController = UIAlertController(title: title, message: message, preferredStyle: UIAlertController.Style.alert)
    alertController.addAction(UIAlertAction.init(title: Dismiss, style: UIAlertAction.Style.destructive, handler: {(alert: UIAlertAction!) in print("Bad")}))
    
    return alertController
}

// MARK: - LFSR Algorithm
func theLowestBit(newState: UInt) -> Character{
    let lastBit = String(newState)
    return lastBit.last!
}

func feedback(initialValue: UInt) -> UInt{
    var newState = initialValue
    
    for _ in 1...8{
        if theLowestBit(newState: newState) == "0"{
            newState = newState>>1
        }
        else{
            newState = (newState>>1) ^ 0x85913829
        }
    }
    return newState
}

func LFSR(data: String, initialValue: UInt)-> String {

    var newState = initialValue
    var hexadecimal : String
    var output = ""
    
    for letter in data {
        
        newState = feedback(initialValue: newState)
        hexadecimal = String(newState ^ (UInt(String(letter))! & 0xFF))
        output.append(hexadecimal)
    }
    return output
}
