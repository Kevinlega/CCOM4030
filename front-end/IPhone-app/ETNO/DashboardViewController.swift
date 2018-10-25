//
//  DashboardViewController.swift
//  ETNO
//
//  Created by Kevin Legarreta on 10/2/18.
//  Copyright © 2018 Los Duendes Malvados. All rights reserved.
//

import UIKit

class DashboardViewController: UIViewController, UICollectionViewDelegate, UICollectionViewDataSource{
   
    // MARK: - Variables

    private let leftAndRightPaddings: CGFloat = 32.0
    private let numberOfItemsPerRow: CGFloat = 2.0
    private let heightAdjustment: CGFloat = 30.0
    
    var Projects = [String]()
    var ButtonArray = [String]()
    
    var name : NSArray = []
    var id : NSArray = []
    var index = 0
    
    var user_id = Int()
    var project_id = Int()
    
    var NoProject = false
    var CantLeave = true

    // MARK: - CollectionView Handlers
    
    func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        if NoProject{
            return 0
        }
        return name.count
    }
    
    func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
        
        let cell = collectionView.dequeueReusableCell(withReuseIdentifier: "cell", for: indexPath) as UICollectionViewCell
        let Label = cell.viewWithTag(1) as! UILabel
        Label.text = (name[indexPath.row] as! String)
    
        return cell
    }
    
    func collectionView(_ collectionView: UICollectionView, didSelectItemAt indexPath: IndexPath) {
        self.project_id = Int(id[indexPath.row] as! String)!
        performSegue(withIdentifier: "ViewProject", sender: nil)
    }
    
    // MARK: - Default Functions
    override func loadViewIfNeeded() {
        let response = GetProjects(user_id: user_id)
        if response["empty"] as! Bool == false{
            self.name = response["project_name"] as! NSArray
            self.id = response["project_id"] as! NSArray
        }
        else{
            self.NoProject = true
        }
    }

    override func viewDidLoad() {
        super.viewDidLoad()
        
        let response = GetProjects(user_id: user_id)
        if response["empty"] as! Bool == false{
            self.name = response["project_name"] as! NSArray
            self.id = response["project_id"] as! NSArray
        }
        else{
            self.NoProject = true
        }
        
//        let width = ((collectionView!.frame.width) - leftAndRightPaddings) / numberOfItemsPerRow
//        let layout = UICollectionViewLayout as! UICollectionViewFlowLayout
//        layout.itemSize = CGSize(width: width, height: width + heightAdjustment)
        
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }

    
    // MARK: - Segue Function

    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if (segue.identifier == "ViewProject"){
            let vc = segue.destination as! ProjectViewController
            vc.user_id = user_id
            vc.project_id = project_id
            
        }
        else if (segue.identifier == "Logout"){
            let _ = segue.destination as! LoginViewController
        }
        else if (segue.identifier == "CreateProject"){
            let vc = segue.destination as! CreateAProjectViewController
            vc.user_id = user_id
        }
    }
}
