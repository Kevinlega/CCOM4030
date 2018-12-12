// Authors     : Luis Fernando
//               Kevin Legarreta
//               David J. Ortiz Rivera
//               Enrique Rodriguez
//
// File        : AddParticipantViewController.swift
// Description : View controller that allows user to add other users to project
// Copyright © 2018 Los Duendes Malvados. All rights reserved.

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
    var usersEmail = [String()]
    // Will Filtered the Users from using the Search Bar
    var FilteredUsers = [String()]
    // Users selected by the admin to add to the project
    var SelectedUsers = [String()]
    var SelectedUsersEmail = [String()]
    
    // Flags to know if the list was empty and if the admin is searching for users.
    var FirstSelected = true
    var Searching = false
    
    // Connections to the app view
    @IBOutlet weak var tableView: UITableView!
    @IBOutlet weak var searchBar: UISearchBar!

    
    // MARK: - table view controller
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        if Searching{
            return FilteredUsers.count
        }
        else if users[0] == ""{
            return 0
        }
        else{
            return users.count
        }
    }
    
    // Display users in table.
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "Row", for: indexPath)
        if Searching{
            cell.textLabel?.text = FilteredUsers[indexPath.row]
        }
        else{
            cell.textLabel?.text = users[indexPath.row]
        }
        
        return cell
    }
    
    // MARK: - Search Bar Actions
    // Use the search bar to search users
    func searchBar(_ searchBar: UISearchBar, textDidChange searchText: String) {
        if searchBar.text == nil || searchBar.text == ""{
            Searching = false
            view.endEditing(true)
        }
        else if (searchBar.text!.contains("@") && searchBar.text!.count > 10 && searchBar.text!.contains(".com")){
            Searching = true
            FilteredUsers = users.filter({$0.localizedCaseInsensitiveContains(searchBar.text!)})
            
        }
        else{
            Searching = false
            FilteredUsers = []
        }
        tableView.reloadData()
    }

    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        if !Searching{
            if let selectedUser = tableView.cellForRow(at: indexPath){
                let indexToDelete = SelectedUsers.firstIndex(of: (selectedUser.textLabel?.text)!)
                if indexToDelete != nil{
                    SelectedUsers.remove(at: indexToDelete!)
                }
                else{
                    SelectedUsers.append((selectedUser.textLabel?.text)!)
                    let emailIndex = users.firstIndex(of: (selectedUser.textLabel?.text)!)
                    SelectedUsersEmail.append(usersEmail[emailIndex!])
                    
                    if FirstSelected {
                        SelectedUsers.remove(at: 0)
                        SelectedUsersEmail.remove(at: 0)
                        FirstSelected = false
                    }
                }
                
                DispatchQueue.main.asyncAfter(deadline: (DispatchTime.now()+0.15), execute: {tableView.reloadData()})
            }
        }
            
        else{
            
            if let selectedUser = tableView.cellForRow(at: indexPath){
                if !(SelectedUsers.contains((selectedUser.textLabel?.text)!)){
                    
                    let emailIndex = users.firstIndex(of: (selectedUser.textLabel?.text)!)
                    SelectedUsersEmail.append(usersEmail[emailIndex!])
                    SelectedUsers.append((selectedUser.textLabel?.text)!)
                    
                    if FirstSelected {
                        SelectedUsers.remove(at: 0)
                        SelectedUsersEmail.remove(at: 0)
                        FirstSelected = false
                    }
                    
                    FilteredUsers = Array(Set(FilteredUsers).subtracting(SelectedUsers))
                    DispatchQueue.main.asyncAfter(deadline: (DispatchTime.now()+0.15), execute: {tableView.reloadData()})
                    
                }
            }
        }
    }
    
    
  // MARK: - Default Functions
  // Get participants from a proect when view loads
    override func viewDidLoad() {
        super.viewDidLoad()
        hideKeyboardWhenTappedAround()
        
        let response = GetParticipants(self: self, project_id: project_id, user_id: user_id)

        if (response["empty"] as? Bool ?? true) == false{
            users = response["names"] as! [String]
            usersEmail = response["emails"] as! [String]
        }
        
        // Do any additional setup after loading the view.
    }
    
    // Default Function
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    // MARK: - Segue Function
    // Prepare user info for next segue
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        
        if segue.identifier != "Logout"{
            let _ = ConnectionTest(self: self)
        }

        // Add particpants to project and move to the project view
        if (segue.identifier == "AddParticipants"){
            if (SelectedUsers.count > 0 && !FirstSelected){
                InsertParticipants(self: self, SelectedEmail: SelectedUsersEmail, project_id: project_id)
        
               let vc = segue.destination as! ProjectViewController
               vc.user_id = user_id
               vc.project_id = project_id
                
            }
            else{
                self.present(Alert(title: "Error", message: "No participant selected.", Dismiss: "Dismiss"),animated: true, completion: nil)
            }
        }
        // Go back to the project view
        else if (segue.identifier == "BackToProject"){
            let vc = segue.destination as! ProjectViewController
            vc.user_id = user_id
            vc.project_id = project_id
        } else if (segue.identifier == "Logout"){
            let _ = segue.destination as! LoginViewController
        }
    }
}
