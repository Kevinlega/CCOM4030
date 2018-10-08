//
//  AddParticipantViewController.swift
//  ETNO
//
//  Created by Kevin Legarreta on 10/2/18.
//  Copyright © 2018 Los 5. All rights reserved.
//

import UIKit

class AddParticipantViewController: UIViewController, UITableViewDelegate, UITableViewDataSource, UISearchBarDelegate{
    
    var UsersCanBeAdded = false
    var project_id = 1
    var users = [String()]
    var FilteredUsers = [String()]
    var SelectedUsers = [String()]
    
    var FirstSelected = true
    var Searching = false
    
    @IBOutlet weak var tableView: UITableView!
    @IBOutlet weak var searchBar: UISearchBar!

    // Verify if we have users to add
    
    @IBAction func CanWeAddUsers(_ sender: Any) {
       
        if (SelectedUsers.count > 0 && !FirstSelected){
            UsersCanBeAdded = true
        }
        else{
            let alertController = UIAlertController(title: "Error", message: "No new participant was selected", preferredStyle: UIAlertController.Style.alert)
            alertController.addAction(UIAlertAction.init(title: "Dismiss", style: UIAlertAction.Style.destructive, handler: {(alert: UIAlertAction!) in print("Bad")}))
            
            self.present(alertController, animated: true, completion: nil)
        }
    }
    


    // Get the Users of the database
    
    func GetUsers(){
        
        var Linkstring = "http://54.81.239.120/API.php?query=1&pid="
        Linkstring += String(project_id)
        let apiLink = URL(string: Linkstring)
        
        let task = URLSession.shared.dataTask(with: apiLink!, completionHandler: {(data, response, error) -> Void in
            do
            {
             
                self.users = try JSONSerialization.jsonObject(with: data!, options: .allowFragments) as! [String]
                
                DispatchQueue.main.async {
                    
                    self.tableView.reloadData()
                
                }
                
            }
            catch{
                // nothing

            }
            })
        task.resume()
        
    }
    
    // Insert new users to the project
    func InsertUsers(){
        var FirstUserID = true
        var UserIdArray = [String()]
        var UserId = [String()]
        
        let GetUserID = "http://54.81.239.120/API.php?query=2&email="
        
        for user in SelectedUsers{
            let apiLink = URL(string: (GetUserID + user))
            
            let group = DispatchGroup()
            group.enter()
            
            let task = URLSession.shared.dataTask(with: apiLink!, completionHandler: {(data, response, error) -> Void in
                do
                {

                    UserId = try JSONSerialization.jsonObject(with: data!, options: .allowFragments) as! [String]
                    if !FirstUserID{
                        UserIdArray.append(contentsOf: UserId)
                    }
                    else{
                        UserIdArray = UserId
                        FirstUserID = false
                    }
                    group.leave()
                }
                catch{
                    // nothing
                    
                }
            })
            task.resume()
            group.wait()
        }
        

        let Linkstring = "http://54.81.239.120/APIinsert.php?query=1&pid=" + String(project_id) + "&uid="
        
        for user in UserIdArray {
            
            let apiLink = URL(string: (Linkstring + user))
            let task = URLSession.shared.dataTask(with: apiLink!, completionHandler: {(data, response, error) -> Void in})
            task.resume()
            
        }
    }
    
    // Work with the table
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        if Searching{
            return FilteredUsers.count
        }
        return SelectedUsers.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "Row", for: indexPath)
        if Searching{
            cell.textLabel?.text = FilteredUsers[indexPath.row]
        }
        else{
            cell.textLabel?.text = SelectedUsers[indexPath.row]
        }
        
        return cell
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        if !Searching{
            if let selectedUser = tableView.cellForRow(at: indexPath){
                let indexToDelete = SelectedUsers.firstIndex(of: (selectedUser.textLabel?.text)!)
                SelectedUsers.remove(at: indexToDelete!)
                DispatchQueue.main.asyncAfter(deadline: (DispatchTime.now()+0.15), execute: {tableView.reloadData()})
                
            }
            }
            
        else{
            
            if let selectedUser = tableView.cellForRow(at: indexPath){
                if !(SelectedUsers.contains((selectedUser.textLabel?.text)!)){
                    
                    
                    SelectedUsers.append((selectedUser.textLabel?.text)!)
                
                    if FirstSelected {
                        SelectedUsers.remove(at: 0)
                        FirstSelected = false
                        }
                
                    FilteredUsers = Array(Set(FilteredUsers).subtracting(SelectedUsers))
                    DispatchQueue.main.asyncAfter(deadline: (DispatchTime.now()+0.15), execute: {tableView.reloadData()})
                    
                    }
                }
            }
        }
    
    // Work with the search bar
    func searchBar(_ searchBar: UISearchBar, textDidChange searchText: String) {
        if searchBar.text == nil || searchBar.text == ""{
            Searching = false
            view.endEditing(true)
        }
        else{
            if (searchBar.text!.contains("@") && searchBar.text!.count > 5){
                Searching = true
                FilteredUsers = users.filter({$0.localizedCaseInsensitiveContains(searchBar.text!)})
                FilteredUsers = Array(Set(FilteredUsers).subtracting(SelectedUsers))
                
                
            }
            else{
                Searching = false
                FilteredUsers = []
            }
        
        }
        tableView.reloadData()
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        GetUsers()
        
        // Do any additional setup after loading the view.
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if (segue.identifier == "AddParticipants"){
            if (UsersCanBeAdded){
               InsertUsers()
               let vc = segue.destination as! ProjectViewController
               vc.selected_project = project_id
            }
        }
        else if (segue.identifier == "BackToProject"){
            let _ = segue.destination as! ProjectViewController
        }
    }
    
    
    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destinationViewController.
        // Pass the selected object to the new view controller.
    }
    */

}
