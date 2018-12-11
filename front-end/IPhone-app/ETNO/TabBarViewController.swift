// Authors     : Luis Fernando
//               Kevin Legarreta
//               David J. Ortiz Rivera
//               Enrique Rodriguez
//
// File        : TabBarViewController.swift
// Description : Store user id in tab bar
// Copyright © 2018 Los Duendes Malvados. All rights reserved.


import UIKit

class TabBarViewController: UITabBarController {
    
    var user_id = Int()
    
    struct User {
       static var uid = Int()
    }
    override func viewDidLoad() {
        super.viewDidLoad()
        User.uid = user_id
        
        // Do any additional setup after loading the view.
    }
}
