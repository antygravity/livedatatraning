package pl.antygravity.livedatatraning

import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu
import android.view.MenuItem
import com.bumptech.glide.Glide
import com.google.gson.GsonBuilder

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class MainActivity : AppCompatActivity(), Callback<User> {

    override fun onResponse(call: Call<User>, response: Response<User>) {

        println(response.body())

        var user: User = response.body()!!

        if (response.isSuccessful) {
            Glide.with(this).load(user.avatarUrl).into(imageView)
        }
    }

    override fun onFailure(call: Call<User>, t: Throwable) {
        t.printStackTrace()
    }

    lateinit var  service : GithubService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        setup()

        button.setOnClickListener { view ->
            val text =  editText.text
            imageView.setImageResource(0)

            val userCall: Call<User> = service.getUser(text.toString())
            userCall.enqueue(this)
        }

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
    }

    private fun setup() {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.github.com/")
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .build()

        service = retrofit.create(GithubService::class.java)
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
