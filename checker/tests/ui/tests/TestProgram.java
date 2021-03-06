import guitypes.checkers.quals.*;
import packagetests.UIByPackageDecl;
import packagetests.SafeByDecl;

public class TestProgram {
    public void nonUIStuff(
                final UIElement e,
                final GenericTaskUIConsumer uicons,
                final GenericTaskSafeConsumer safecons) {
        //:: error: (call.invalid.ui)
        e.dangerous(); // should be bad
        e.runOnUIThread(new IAsyncUITask() {
            final UIElement e2 = e;
            public void doStuff() { // should inherit UI effect
                e2.dangerous(); // should be okay
            }
        });
        uicons.runAsync(new @UI IGenericTask() {
            final UIElement e2 = e;
            public void doGenericStuff() { // Should be inst. w/ @UI eff.
                e2.dangerous(); // should be okay
            }
        });
        safecons.runAsync(new @AlwaysSafe IGenericTask() {
            final UIElement e2 = e;
            public void doGenericStuff() { // Should be inst. w/ @AlwaysSafe
                //:: error: (call.invalid.ui)
                e2.dangerous(); // should be an error
                safecons.runAsync(this); // Should be okay, this:@AlwaysSafe
            }
        });
        //:: error: (argument.type.incompatible)
        safecons.runAsync(new @UI IGenericTask() {
            final UIElement e2 = e;
            public void doGenericStuff() { // Should be inst. w/ @UI
                e2.dangerous(); // should be ok
                //:: error: (argument.type.incompatible)
                safecons.runAsync(this); // Should be error, this:@UI
            }
        });
        safecons.runAsync(new IGenericTask() {
            public void doGenericStuff() {
                // Test that the package annotation works
                //:: error: (call.invalid.ui)
                UIByPackageDecl.implicitlyUI();
                // Test that @SafeType works: SafeByDecl is inside a @UIPackage
                SafeByDecl.safeByTypeDespiteUIPackage();
            }
        });
        safecons.runAsync(new IGenericTask() {
            //:: error: (conflicts.override)
            @UIEffect public void doGenericStuff() {
                UIByPackageDecl.implicitlyUI();
            }
        });
    }
}
