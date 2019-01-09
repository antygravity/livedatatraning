package pl.antygravity.livedatatraning

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.Transformations
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class MainActivity : AppCompatActivity() {

    val TAG = MainActivity::class.java.simpleName

    private val login = MutableLiveData<String>()

    lateinit var user: LiveData<ApiResponse<User>>


    lateinit var service: GithubService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        setup()

        button.setOnClickListener { view ->
            val text : String = editText.text.toString()
            imageView.setImageResource(0)

            login.value = text
        }

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
    }

    private fun setup() {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.github.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(LiveDataCallAdapterFactory())
            .build()

        service = retrofit.create(GithubService::class.java)
        user = Transformations.switchMap(login) {log ->
            if (log == null) {
                AbsentLiveData.create()
            } else {
                service.getUser(log)
            }

        }

        user.observe(this, Observer { apiResponse ->
                when (apiResponse) {
                    is ApiSuccessResponse -> {

                        val user : User =  apiResponse.body

                        Log.d(TAG, "User: " + user)

                       Glide.with(this).load(user.avatarUrl).into(imageView)
                    }

                    is ApiEmptyResponse -> {

                    }
                    is ApiErrorResponse -> {
                        Log.e(TAG, "Error" + apiResponse.errorMessage)

                    }
                }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}
