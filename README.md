# ModularAdapter

The `RecyclerView.Adapter` that makes your life easy!

 - **Improves your architecture**: The modular nature of the `ModularAdapter` allows splitting up your `Adapters` into a few very small and simple components making it easy for your to create beautiful lists.
 - **Simple to use, almost no boilerplate**: Creating an `Adapter` for a `RecyclerView` has never been this simple. Just a few lines of code and picking an appropriate `ItemManager` implementation is all you need to do.
 - **Takes care of the heavy lifting**: By introducing `ItemManager` components the `ModularAdapter` allows you to accomplish complex behaviors in your `RecyclerView` with just a few lines of code.
 - **Extremely lightweight**: `ModularAdapter` is not a huge library with tons of functionality you are never going to need. The footprint of this library is tiny and you only need to include exactly the functionality you are going to need into your project.  

[![Build Status](https://travis-ci.org/Wrdlbrnft/ModularAdapter.svg?branch=master)](https://travis-ci.org/Wrdlbrnft/ModularAdapter)
[![BCH compliance](https://bettercodehub.com/edge/badge/Wrdlbrnft/ModularAdapter)](https://bettercodehub.com/)

# How do I add it to my project?

Just add this dependency to your build.gradle file:

```
compile 'com.github.wrdlbrnft:modular-adapter:0.2.0.6'
```

# How do I use it?

## Implementing the Adapter

There are two ways to create a `ModularAdapter` in your project. First you can subclass it like any other `Adapter`:

```java
public class ExampleAdapter extends ModularAdapter<ExampleModel> {

    public ExampleAdapter(Context context, ItemManager<ExampleModel> itemManager) {
        super(context, itemManager);
    }

    @Override
    protected ViewHolder<? extends ExampleModel> onCreateViewHolder(LayoutInflater inflater, ViewGroup parent, int viewType) {
        final View itemView = inflater.inflate(R.layout.item_example, parent, false);
        return new ExampleViewHolder(itemView);
    }
}
```

As you can see above in that case all you have to do is implement the `onCreateViewHolder()` method. The `ModularAdapter` takes care of binding the data.

However you can also quickly create a `ModularAdapter` using the `ModularAdapter.Builder` class:

```java
final ModularAdapter<ExampleModel> adapter = new ModularAdapter.Builder<>(context, itemManager)
        .add(ExampleModel.class, new ModularAdapter.ViewHolderFactory<ModularAdapter.ViewHolder<ExampleModel>>() {
            @Override
            public ModularAdapter.ViewHolder<ExampleModel> create(LayoutInflater layoutInflater, ViewGroup parent) {
                final View itemView = inflater.inflate(R.layout.item_example, parent, false);
                return new ExampleViewHolder(itemView);
            }
        })
        .build();
```

Both ways are equivalent, however when you have lists with many simple models the second method is usually favourable. If you are reusing the Adapter in many places and/or want to implement complex behavior you should go with the first method.

The `ItemManager` instance you can see in the constructors above is the data source for a `ModularAdapter` instance. It takes care of providing the data to the `ModularAdapter` as well as notifying it about changes. The `ModularAdapter` comes with a few different implementations for most use cases and if you need something special you can implement it yourself!

You can find more information about the different provided `ItemManager` implementations further down on this page.

## Implementing the ViewHolders

ViewHolders used in a `ModularAdapter` have to be subclasses of `ModularAdapter.ViewHolder`. They are responsible for binding the data to the `View`s in your `RecyclerView`. An example implementation looks like this:

```java
public class ExampleViewHolder extends ModularAdapter.ViewHolder<ExampleModel> {

    private final TextView mValueView;

    public ExampleViewHolder(View itemView) {
        super(itemView);
        
        mValueView = itemView.findViewById(R.id.value);
    }

    @Override
    protected void performBind(ExampleModel item) {
        mValueView.setText(item.getValue());
    }
}
```

The method `performBind()` is called by the `ModularAdapter` when it is necessary to bind new data to the `View` managed by your `ViewHolder`.

# About ItemManagers

`ItemManager`s are responsible for managing items for the `ModularAdapter`. They hold the data that is supposed to be displayed in the `RecyclerView` and take care of notifying the `ModularAdapter` about changes to that data.

Currently there are two `ItemManager` implementations available right out of the box:

 - `StaticListItemManager`: A very simple `ItemManager` implementation that just displays a `List` of items. You should use this whenever you have a predefined unchanging `List` of models - for example for a known list of options the user can select one of. Changes to that `List` are not supported by the `StaticListItemManager`.
 - `SortedListItemManager`: This `ItemManager` displays a dynamic collection of items in a sorted order determined by a `Comparator`. It is useful whenever you want to display a `List` of models in a particular order - for example sorted by date. Another thing this `ItemManager` excels at is high performance filtering and random changes to the underlying data even for very long lists up to 100.000 items. All changes to the data are automatically fully animated in the `RecyclerView`.  

You can find more information about these `ItemManager` implementations below.

## StaticListItemManager

This `ItemManager` is useful for displaying a static `List` of items. To use the `StaticListItemManager` add this dependency to your build.gradle file:

```groovy
compile 'com.github.wrdlbrnft:static-list-item-manager:0.2.0.6'
```

After that you can use it in your code like this:

```java
final List<ExampleModel> models = ...

final ItemManager<ExampleModel> itemManager = new StaticListItemManager<>(models);
final ExampleAdapter adapter = new ExampleAdapter(context, itemManager);
recyclerView.setAdapter(adapter);
```

This `ItemManager` does not support making any changes to the `List` of items once it has been created.

## SortedListItemManager

This `ItemManager` is useful whenever you want to display a `List` of models in a particular order - for example sorted by date. Another thing this `ItemManager` excels at is high performance filtering and random changes to the underlying data even for very long lists up to 100.000 items. All changes to the data are automatically fully animated in the `RecyclerView`.

To use the `SortedListItemManager` just add this dependency to your build.gradle file:

```groovy
compile 'com.github.wrdlbrnft:sorted-list-item-manager:0.2.0.6'
```

You need two things for the `SortedListItemManager` to perform its magic:
 
 - Every model class managed by the `SortedListItemManager` has to implement the `SortedListItemManager.ViewModel` interface.
 - You need a define a `Comparator` which determines the order of the items in your `RecyclerView`

### Implementing the SortedListItemManager.ViewModel interface

All models managed by the `SortedListItemManager` have to implement the `SortedListItemManager.ViewModel` interface. This interface requires you to implement two methods in your model:

 1. **`isSameModelAs()`**: This method is used to determine if two models refer to the same thing. Imagine it like this: If you have a list of movies and you update the list (for example when the user is filtering the list) `isSameModelAs()` will be used by the `SortedListItemManager` to determine if the same model is still present in the list.
 2. **`isContentTheSameAs()`**: This method is used to determine if the content of a model is equal to some other model. For example after two "same" model have been found using `isSameModelAs()` then `isContentTheSameAs()` will be used to check if data in the model has been modified and if a change animation has to be played in the `RecylerView`.
 
The canonical way to implement the above methods is like this:

```java
public class ExampleModel implements SortedListItemManager.ViewModel {

    private final long mId;
    private final String mValue;

    public ExampleModel(long id, String value) {
        mId = id;
        mValue = value;
    }

    public long getId() {
        return mId;
    }

    public String getValue() {
        return mValue;
    }

    @Override
    public <T> boolean isSameModelAs(T item) {
        if (item instanceof ExampleModel) {
            final ExampleModel other = (ExampleModel) item;
            return other.mId == mId;
        }
        return false;
    }

    @Override
    public <T> boolean isContentTheSameAs(T item) {
        if (item instanceof ExampleModel) {
            final ExampleModel other = (ExampleModel) item;
            return mValue != null ? mValue.equals(other.mValue) : other.mValue == null;
        }
        return false;
    }
}
```

### Defining a Comparator for your SortedListItemManager

The `Comparator` used by the `SortedListItemManager` can be implemented like any `Comparator` that would be used for sorting:

```java
final Comparator<ExampleModel> alphabeticalComparator = new Comparator<ExampleModel>() {
    @Override
    public int compare(ExampleModel a, ExampleModel b) {
        return a.getText().compareTo(b.getText());
    }
};
```

If you display only one type of model in your `RecyclerView` this is most of the time all you need. However for more complex situations in which you are dealing with many different types of models and/or have complex sorting logic the `SortedListItemManager` comes with a handy tool to simplify the job: The `ComparatorBuilder` class.

It can be used like this:

```java
final Comparator<ExampleModel> comparator = new ComparatorBuilder<>()
        .setGeneralOrder(SomeModel.class, AnotherModel.class)
        .setOrderForModel(SomeModel.class, new Comparator<SomeModel>() {
            @Override
            public int compare(SomeModel a, SomeModel b) {
                return a.getText().compareTo(b.getText());
            }
        })
        .setOrderForModel(AnotherModel.class, new Comparator<AnotherModel>() {
            @Override
            public int compare(AnotherModel a, AnotherModel b) {
                return Integer.signum(a.getRank() - b.getRank());
            }
        })
        .build();
```

In the above example `setGeneralOrder()` is used to set the order of models based on their type. In this specific example it means all `SomeModel` models will appear before `AnotherModel` models. The `setOrderForModel()` calls below it are used to set how each type of model should be ordered specifically. In this case it means that `SomeModel` instances are ordered by comparing their text field and `AnotherModel` instances are ordered based on their rank field.

### Using the SortedListItemManager

Once you have implemented the `SortedListItemManager.ViewModel` interface in your models and created a `Comparator` you can use the `SortedListItemManager` like this:

```java
final ItemManager<ExampleModel> itemManager = new SortedListItemManager(ExampleModel.class, comparator);
final ExampleAdapter adapter = new ExampleAdapter(context, itemManager);
recyclerView.setAdapter(adapter);
```

Updating the items displayed in the `RecyclerView` works like this: 

```java
final List<ExampleModel> models = ...

itemManager.newTransaction()
        .replaceAll(models)
        .commit();
```

This will automatically animate any changes to the data and it is considered best practice to just use the above code to update the whole list of items in the `SortedListItemManager` whenever there is a change to the data (for example when filtering).

In the above code `newTransaction()` creates a new `ModifiableItemManager.Transaction`. By calling methods like `replaceAll()` on the Transaction you can modify data in the `RecyclerView` however any changes you make are only executed and animated on screen once you call `commit()`.

Besides `replaceAll()` there are other methods you can use to modify data in a `Transaction`:

 - `add()`: Adds one or a `Collection` of models. If a model added this way already exists in the `RecyclerView` it will be updated if necessary.
 - `remove()`: Removes one or a `Collection` of models.
 - `removeAll()`: Removes all models from the `RecyclerView`
 
Of course you can mix and match as many of these calls together in one `Transaction` however the simpler your transactions the better.

### Dealing with large lists

If you manage 25.000+ items with a `SortedListItemManager` the `Transaction` may take up to half a second to be executed, but you don't need to worry about blocking the UI thread. The `SortedListItemManager` does all its work efficiently in a separate `Thread` so your UI will stay fluid and responsive.  
However you might want to show a progress spinner or some other loading indicator to the user while the `SortedListItemManager` does its thing. For especially this use case you can use the `StateCallback` of the `SortedListItemManager`. You can consume it like this:

```java
itemManager.addStateCallback(new ItemManager.StateCallback() {
    @Override
    public void onChangesInProgress() {
        // A Transaction is in progress so show a loading indicator
    }

    @Override
    public void onChangesFinished() {
        // All pending Transactions have been executed so hide the loading indicator again
    }
});
```