import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.students.Student
import com.example.students.R

class StudentAdapter(
    private val students: List<Student>, // List of students
    private val onItemClick: (Student) -> Unit // Callback for item clicks
) : RecyclerView.Adapter<StudentAdapter.StudentViewHolder>() {

    // ViewHolder to hold the view for each item
    inner class StudentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val studentImage: ImageView = itemView.findViewById(R.id.studentImage)
        val studentName: TextView = itemView.findViewById(R.id.studentName)
        val studentId: TextView = itemView.findViewById(R.id.studentId)
        val studentCheckbox: CheckBox = itemView.findViewById(R.id.studentCheckbox)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentViewHolder {
        // Inflate the layout for each item
        val view = LayoutInflater.from(parent.context).inflate(R.layout.student_item, parent, false)
        return StudentViewHolder(view)
    }

    override fun onBindViewHolder(holder: StudentViewHolder, position: Int) {
        // Bind the data to the ViewHolder
        val student = students[position]
        holder.studentImage.setImageResource(student.image)
        holder.studentName.text = student.name
        holder.studentId.text = student.id
        holder.studentCheckbox.isChecked = student.isChecked

        // Handle checkbox toggle
        holder.studentCheckbox.setOnCheckedChangeListener { _, isChecked ->
            student.isChecked = isChecked
        }

        // Handle item click
        holder.itemView.setOnClickListener {
            onItemClick(student)
        }
    }

    override fun getItemCount(): Int {
        return students.size
    }
}
