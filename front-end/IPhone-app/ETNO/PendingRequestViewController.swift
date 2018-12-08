// Authors     : Luis Fernando
//               Kevin Legarreta
//               David J. Ortiz Rivera
//               Bryan Pesquera
//               Enrique Rodriguez
//
// File        : PendingRequestViewController.swift
// Description : View controller for pending request view that s
//               lets the user accept and reject friend requests.
// Copyright © 2018 Los Duendes Malvados. All rights reserved.

import UIKit

class PendingRequestViewController: UIViewController, UITableViewDelegate, UITableViewDataSource, UISearchBarDelegate {
    
    // MARK: - Variables
    // Flag that indicates if the admin has users to be added
    var UsersCanBeAdded = false
    var UsersCanBeRemoved = false
    
    // Variables passed from previous view
    var user_id = Int()
    var project_id = Int()
    
    // List of every user in the data base that is not in the project
    var pendingUsers = [String()]
    var pendingEmail = [String()]
    
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
    
    // Verifies if a friend Request can be accepted or rejected
    @IBAction func SendRequest(_ sender: Any) {
        if (SelectedUsers.count > 0 && !FirstSelected){
            UsersCanBeAdded = true
            UsersCanBeRemoved = false
        }
        else{
            self.present(Alert(title: "Error", message: "No one selected.", Dismiss: "Dismiss"),animated: true, completion: nil)
        }
    }
    
    // Rejects Request
    @IBAction func RemoveRequest(_ sender: Any) {
        if (SelectedUsers.count > 0 && !FirstSelected){
            UsersCanBeRemoved = true
            UsersCanBeAdded = false
        }
        else{
            self.present(Alert(title: "Error", message: "No one selected.", Dismiss: "Dismiss"),animated: true, completion: nil)
        }
    }
    
    
    // MARK: - Modify the Tableview
    // Update the view of the table
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        if Searching{
            return FilteredUsers.count
        }
        else if !FirstSelected{
            if pendingUsers[0] == ""{
                return 0
            } else {
                return pendingUsers.count
            }
        }
        else{
            return pendingUsers.count
        }
    }
    
    // Writes the data in the cell
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "Row", for: indexPath)
        if Searching{
            cell.textLabel?.text = FilteredUsers[indexPath.row]
        }
        else{
            cell.textLabel?.text = pendingUsers[indexPath.row]
        }
        
        return cell
    }
    
    // Selects the cell and adds them to the selected list
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        if !Searching{
            if let selectedUser = tableView.cellForRow(at: indexPath){
                let indexToDelete = SelectedUsers.firstIndex(of: (selectedUser.textLabel?.text)!)
                if indexToDelete != nil{
                    SelectedUsers.remove(at: indexToDelete!)
                }
                else{
                    SelectedUsers.append((selectedUser.textLabel?.text)!)
                    let emailIndex = pendingUsers.firstIndex(of: (selectedUser.textLabel?.text)!)
                    SelectedUsersEmail.append(pendingEmail[emailIndex!])
                    
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
                    
                     let emailIndex = pendingUsers.firstIndex(of: (selectedUser.textLabel?.text)!)
                    SelectedUsersEmail.append(pendingEmail[emailIndex!])
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
    
    // MARK: - Search Bar Actions
    // Use the search bar to search users
    func searchBar(_ searchBar: UISearchBar, textDidChange searchText: String) {
        if searchBar.text == nil || searchBar.text == ""{
            Searching = false
            view.endEditing(true)
        }
        else if (searchBar.text!.contains("@") && searchBar.text!.count > 5){
            Searching = true
            FilteredUsers = pendingUsers.filter({$0.localizedCaseInsensitiveContains(searchBar.text!)})
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
        hideKeyboardWhenTappedAround()
        user_id = TabBarViewController.User.uid

        let response = GetPendingRequest(user_id: user_id)
        if response["empty"] as! Bool == false{
            FirstSelected = true
            pendingUsers = response["name"] as! [String]
            pendingEmail = response["email"] as! [String]
        }
        
        // Do any additional setup after loading the view.
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    // MARK: - Segue Function
    // Handles the data
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        
        ConnectionTest(self: self)

        // Send friend request
        if (segue.identifier == "SendFriendRequestAnswer"){
            if(UsersCanBeAdded){
                let response = AnswerRequest(user_id: user_id, SelectedUsersEmail: SelectedUsersEmail)
                print(response)
                if (response["success"] as! Bool) == true{
                    let vc = segue.destination as! DashboardViewController
                    vc.user_id = user_id
                }
            }
            else{
                self.present(Alert(title: "Error", message: "No participant selected.", Dismiss: "Dismiss"),animated: true, completion: nil)
            }
        } // Go back to the Dashboard view
        else if (segue.identifier == "BackToDashboard"){
            let vc = segue.destination as! DashboardViewController
            vc.user_id = user_id
        } else if (segue.identifier == "RejectRequest"){
            if(UsersCanBeRemoved){
                let response = DeclineRequest(user_id: user_id, SelectedUsersEmail: SelectedUsersEmail)
                print(response)
                if (response["success"] as! Bool) == true{
                    let vc = segue.destination as! DashboardViewController
                    vc.user_id = user_id
                }
            }
        }
    }
}
