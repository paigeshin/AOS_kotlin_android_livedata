# LiveData

- A lifecycle aware observable data holder class.
- **LiveData only updates observers in an active lifecycle state.**
- Automatically update UI when app data changes.
- No need to write codes to handle lifecycle manually.

# LiveData vs MutableLiveData

- Data in a LiveData object are only readable, not editable.
- A MutableLiveData object allows us to change/update its data.

# LiveData(MutableLiveData Implementation)

- ViewModel, Before refactoring

```kotlin
class MainActivityViewModel(startingTotal: Int) : ViewModel() {
    private var total = 0

    init {
        total = startingTotal
    }

    fun getTotal():Int{
        return total
    }

    fun setTotal(input:Int){
        total +=input
    }
}
```

- ViewModel, After refactoring

```kotlin
class MainActivityViewModel(startingTotal : Int) : ViewModel() {

    var total: MutableLiveData<Int> = MutableLiveData<Int>()

    init {
        total.value = startingTotal
    }

    fun setTotal(input:Int){
        total.value = (total.value)?.plus(input)
    }

}
```

- Activity, Before refactoring

```kotlin
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var viewModel: MainActivityViewModel
    private lateinit var viewModelFactory: MainActivityViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        /** Initialize ViewModelFactory **/
        viewModelFactory = MainActivityViewModelFactory(125)

        /** Initialize ViewModel With Factory Instance **/
        viewModel = ViewModelProvider(this, viewModelFactory).get(MainActivityViewModel::class.java)

        binding.resultTextView.text = viewModel.getTotal().toString()

        binding.insertButton.setOnClickListener {
            viewModel.setTotal(binding.inputEditText.text.toString().toInt())
            binding.resultTextView.text = viewModel.getTotal().toString()

        }

    }
}
```

- Activity, After refactoring

```kotlin
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainActivityViewModel
    private lateinit var viewModelFactory: MainActivityViewModelFactory
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        viewModelFactory = MainActivityViewModelFactory(125)

        viewModel = ViewModelProvider(this,viewModelFactory).get(MainActivityViewModel::class.java)

        //Using MutableLiveData.. observe data changes.
        viewModel.total.observe(this@MainActivity, Observer {
            binding.resultTextView.text = it.toString()
        })

        binding.insertButton.setOnClickListener {
            viewModel.setTotal(binding.inputEditText.text.toString().toInt())
        }

    }
}
```

### Encapsulate ViewModel

- ViewModel

```kotlin
class MainActivityViewModel(startingTotal : Int) : ViewModel() {

    private var total: MutableLiveData<Int> = MutableLiveData<Int>()
    val totalData: LiveData<Int>
    get() = total

    init {
        total.value = startingTotal
    }

    fun setTotal(input:Int){
        total.value = (total.value)?.plus(input)
    }

}
```

- Activity

```kotlin
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainActivityViewModel
    private lateinit var viewModelFactory: MainActivityViewModelFactory
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        viewModelFactory = MainActivityViewModelFactory(125)

        viewModel = ViewModelProvider(this,viewModelFactory).get(MainActivityViewModel::class.java)

        //Using MutableLiveData.. observe data changes.
        viewModel.totalData.observe(this@MainActivity, Observer {
            binding.resultTextView.text = it.toString()
        })

        binding.insertButton.setOnClickListener {
            viewModel.setTotal(binding.inputEditText.text.toString().toInt())
        }

    }
}
```

# Another Example

- MainActivityViewModel

```kotlin

class MainActivityViewModel : ViewModel() {
    private var count = MutableLiveData<Int>()
    val currentCount: LiveData<Int>
    get() = count

    init {
        count.value = 0
    }

    fun updateCount(){
        count.value?.let {
            count.value = it + 1
        }
    }
}
```

- MainActivity

```kotlin
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainActivityViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        viewModel = ViewModelProvider(this).get(MainActivityViewModel::class.java)

        viewModel.currentCount.observe(this, Observer {
            binding.countText.text = it.toString()
        })

        binding.button.setOnClickListener {
            viewModel.updateCount()
        }
    }
}
```