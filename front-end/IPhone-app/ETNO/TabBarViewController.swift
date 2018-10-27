//
//  TabBarViewController.swift
//  ETNO
//
//  Created by Kevin Legarreta on 10/26/18.
//  Copyright © 2018 Los 5. All rights reserved.
//

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
