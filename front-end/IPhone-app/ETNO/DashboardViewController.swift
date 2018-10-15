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
    
    
    // MARK: - Connection to Database
    func GetProjects() {
        
        // Create the request to the API
        let QueryType = "3"
        let url = URL(string: "http://54.81.239.120/selectAPI.php")
        var request = URLRequest(url:url!)
        request.httpMethod = "POST"
        let post = "queryType=\(QueryType)&uid=\(user_id)"
        request.httpBody = post.data(using: String.Encoding.utf8)
        
        let group = DispatchGroup()
        group.enter()
        
        let task = URLSession.shared.dataTask(with: request) { (data: Data?, response: URLResponse?, error: Error?) in
            do {
                let json = try? JSONSerialization.jsonObject(with: data!, options: .allowFragments) as? NSDictionary
                
                if let parseJSON = json {
                    if parseJSON!["empty"] as! String == "no"{
                        self.name = parseJSON!["project_name"] as! NSArray
                        self.id = parseJSON!["project_id"] as! NSArray
                    }
                    else{
                        self.NoProject = true
                    }
                }
            }
            group.leave()
        }
        task.resume()
        group.wait()
    }
    
    // MARK: - Default Functions
    override func loadViewIfNeeded() {
        GetProjects()
    }

    override func viewDidLoad() {
        super.viewDidLoad()
        
        GetProjects()
        
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
