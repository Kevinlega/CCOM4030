//
//  AddParticipantViewController.swift
//  ETNO
//
//  Created by Kevin Legarreta on 10/2/18.
//  Copyright © 2018 Los Duendes Malvados. All rights reserved.
//

import UIKit

class AddParticipantViewController: UIViewController, UITableViewDelegate, UITableViewDataSource, UISearchBarDelegate{
    
    // MARK: - Variables
    // Flag that indicates if the admin has users to be added
    var UsersCanBeAdded = false
    
    // Variables passed from previous view
    var user_id = Int()
    var project_id = Int()
    
    // List of every user in the data base that is not in the project
    var users = [String()]
    // Will Filtered the Users from using the Search Bar
    var FilteredUsers = [String()]
    // Users selected by the admin to add to the project
    var SelectedUsers = [String()]
    
    // Flags to know if the list was empty and if the admin is searching for users.
    var FirstSelected = true
    var Searching = false
    
    // Connections to the app view
    @IBOutlet weak var tableView: UITableView!
    @IBOutlet weak var searchBar: UISearchBar!

    
    // MARK: - Add Participant Action (Button Press)
    // Verify if we have users to add and alert if not
    
    @IBAction func CanWeAddUsers(_ sender: Any) {
       
        if (SelectedUsers.count > 0 && !FirstSelected){
            UsersCanBeAdded = true
        }
        else{
            self.present(Alert(title: "Error", message: "No participant selected.", Dismiss: "Dismiss"),animated: true, completion: nil)
        }
    }
    

    // MARK: - Retrieve Users
    // Get the Users from the database
    
    func GetUsers(){
        
        let QueryType = "1";
        let url = URL(string: "http://54.81.239.120/selectAPI.php");
        var request = URLRequest(url:url!)
        
        request.httpMethod = "POST"
        let post = "queryType=\(QueryType)&pid=\(1)";
        request.httpBody = post.data(using: String.Encoding.utf8);
        print(post)
        let task = URLSession.shared.dataTask(with: request) { (data: Data?, response: URLResponse?, error: Error?) in
            do
            {
                self.users = try JSONSerialization.jsonObject(with: data!, options: .allowFragments) as! [String]
               
                DispatchQueue.main.async {
                    self.tableView.reloadData()
                }
            }catch{
                print("bad")
                }
            }
        task.resume()
    }
    
    // MARK: - Insert Users
    // Insert new users to the project
    
    func InsertUsers(){
        
        var FirstUserID = true
        var UserIdArray = [String()]
        var UserId = [String()]
        
        // Create the request to the API
        var QueryType = "2";
        var url = URL(string: "http://54.81.239.120/selectAPI.php");

        
        for user in SelectedUsers{
            var request = URLRequest(url:url!)
            
            request.httpMethod = "POST"
            let post = "queryType=\(QueryType)&email=\(user)";
            request.httpBody = post.data(using: String.Encoding.utf8);
            
            let group = DispatchGroup()
            group.enter()
            
            let task = URLSession.shared.dataTask(with: request) { (data: Data?, response: URLResponse?, error: Error?) in
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
                }catch{
                    }
                }
            task.resume()
            group.wait()
            }
        
        QueryType = "1";
        url = URL(string: "http://54.81.239.120/insertAPI.php");
        var request = URLRequest(url:url!)
        
        request.httpMethod = "POST"
        
        for user in UserIdArray {
            
            let post = "queryType=\(QueryType)&pid=\(String(self.project_id))&uid=\(user)"
            print(post)
            request.httpBody = post.data(using: String.Encoding.utf8)
            let task = URLSession.shared.dataTask(with: request) { (data: Data?, response: URLResponse?, error: Error?) in }
            task.resume()
        }
    }
    
    // MARK: - Modify the Tableview
    // Update the view of the table
    
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
    
    // MARK: - Search Bar Actions
    
    // Use the search bar to search users
    func searchBar(_ searchBar: UISearchBar, textDidChange searchText: String) {
        if searchBar.text == nil || searchBar.text == ""{
            Searching = false
            view.endEditing(true)
        }
        else if (searchBar.text!.contains("@") && searchBar.text!.count > 5){
                Searching = true
                FilteredUsers = users.filter({$0.localizedCaseInsensitiveContains(searchBar.text!)})
                FilteredUsers = Array(Set(FilteredUsers).subtracting(SelectedUsers))

            }
            else{
                Searching = false
                FilteredUsers = []
            }
        tableView.reloadData()
    }

  // MARK: - Default Functions
    
    override func viewDidLoad() {
        super.viewDidLoad()
        GetUsers()
        
        // Do any additional setup after loading the view.
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    // MARK: - Segue Function
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
//        Add particpants to project and move to the project view
        if (segue.identifier == "AddParticipants"){
            if (UsersCanBeAdded){
               InsertUsers()
               let vc = segue.destination as! ProjectViewController
               vc.user_id = user_id
               vc.project_id = project_id
            }
        } // Go back to the project view
        else if (segue.identifier == "BackToProject"){
            let vc = segue.destination as! ProjectViewController
            vc.user_id = user_id
            vc.project_id = project_id
        }
    }
}
