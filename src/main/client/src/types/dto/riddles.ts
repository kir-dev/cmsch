export interface Riddle {
  id: Number
  title: String
  imageUrl: String
  solved: Boolean
  hint?: String
}

export interface RiddleCategory {
  id: Number
  name: String
  completed: Boolean
  scoore: Number
  nextRiddle?: Number
}
